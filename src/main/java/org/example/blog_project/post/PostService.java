package org.example.blog_project.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.member.Member;
import org.example.blog_project.member.MemberRepository;
import org.example.blog_project.post.dto.*;
import org.example.blog_project.post_image.PostImageService;
import org.example.blog_project.post_tag.PostTag;
import org.example.blog_project.post_tag.PostTagRepository;
import org.example.blog_project.post_tag.PostTagService;
import org.example.blog_project.read_post.ReadPost;
import org.example.blog_project.read_post.ReadPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    @Value("${main.file.path}")
    private String uploadDir;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImageService postImageService;
    private final PostTagService postTagService;
    private final PostTagRepository postTagRepository;
    private final ReadPostRepository readPostRepository;

    @Transactional
    public CreatePostResDto createPost(Long memberId, PostForm postForm, List<MultipartFile> files){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
        Post post = Post.builder()
                .title(postForm.getTitle())
                .content(postForm.getContent())
                .isTemp(postForm.getIsTemp())
                .member(member)
                .build();
        Post savedPost = postRepository.save(post);

        if(postForm.getTags()!=null){
            postTagService.createPostTag(savedPost,postForm.getTags());
        }


        // files가 null일 경우 빈 리스트로 초기화
        if (files == null) {
            files = Collections.emptyList();
        }

        postImageService.createPostImage(post,files);

        log.info("Post created with ID: " + post.getPostId() + ", isTemp: " + post.getIsTemp());


        return CreatePostResDto.builder()
                .postId(post.getPostId())
                .isTemp(post.getIsTemp())
                .build();
    }

    @Transactional
    public String publishPost(PublishForm form, MultipartFile file,Long memberId) throws Exception {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글"));
        if(!memberId.equals(post.getMember().getMemberId())){
            return "잘못된 사용자";
        }

        String encodedInput = URLEncoder.encode(form.getPostUrl(), "UTF-8");
        String url = String.format("/@%s/%s", post.getMember().getLoginId(), encodedInput);
        // URL 중복 검사
        if (postRepository.findByPostUrl(url).isPresent()) {
            throw new IllegalStateException("URL이 이미 사용 중입니다.");
        }

        post.setPostUrl(url);
        post.setTemp(false);
        post.setIntroduce(form.getIntroduce());
        post.setisHide(form.getIsHide());
        post.setSeries(form.getSeries());

        if(file != null){
            if(post.getMainImageUrl()!=null){
                deleteFile(form.getPostId(), post.getMainImageUrl());
            }
            String fileName = saveFile(file);
            post.setMainImageUrl(fileName);
        }
        postRepository.save(post);
        return post.getPostUrl();
    }

    @Transactional
    public String updatePost(PostForm form,Long postId,List<MultipartFile> files, Long memberId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글"));
        if(!memberId.equals(post.getMember().getMemberId())){
            return "잘못된 사용자";
        }

        post.updateTitle(form.getTitle());
        post.updateContent(form.getContent());
        post.setTemp(form.getIsTemp());

        postTagRepository.deleteAllByPost(post);
        post.getPostTagList().clear(); //arrayList 비우기
        if(form.getTags()!=null){
            postTagService.createPostTag(post, form.getTags());
        }
        try {
            // 이미지 처리: PostImageService를 사용하여 이미지 업데이트
            postImageService.updatePostImages(post, files);
        }catch (IOException e){
            log.info(e.getMessage());
        }

        postRepository.save(post);
        return "수정 성공";
    }

    @Transactional
    public String deletePost(Long postId,Long memberId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글"));
        if(!memberId.equals(post.getMember().getMemberId())){
            return "잘못된 사용자";
        }

        postTagService.deletePostTag(post);
        postRepository.delete(post);

        return "삭제 성공";
    }

    @Transactional
    public boolean deleteFile(Long postId, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글"));

            if (post.getMainImageUrl() == null) {
                return false;
            }

            post.setMainImageUrl(null);
            postRepository.save(post);
            Path path = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(path);
            System.out.println("File deleted: " + path.toString());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
            return false;
        }
    }


    private String saveFile(MultipartFile file) {
        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save the file
            file.transferTo(filePath.toFile());

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public DetailPostDto getDetailPost(String loginId, String decodedTitle){

        String url = "/@" + loginId + "/" + decodedTitle;

        Post post = postRepository.findByPostUrl(url)
                .orElseThrow(() -> new RuntimeException("존재하지않는 게시글"));
        Member member = memberRepository.findMemberByLoginId(loginId);

        log.info("Loaded post with series: " + post.getSeries());  // 시리즈 정보 로
        Optional<Post> previousPost = getPreviousPost(post.getMember().getMemberId(), post.getPostId());
        Optional<Post> nextPost = getNextPost(post.getMember().getMemberId(), post.getPostId());

        log.info("postURl: "+post.getPostUrl());
        log.info("postImageUrl : "+post.getPostImageList().get(0).getImageUrl());



        return DetailPostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .author(post.getMember().getName())
                .createdAt(post.getCreatedAt())
                .postTagList(post.postTagList)
                .series(post.getSeries())
                .content(post.getContent())
                .postImageList(post.postImageList)
                .prePostUrl(previousPost.map(Post::getPostUrl).orElse(null))
                .nextPostUrl(nextPost.map(Post::getPostUrl).orElse(null))
                .likesList(post.getLikesList())
                .commentList(post.getCommentList())
                .build();
    }

    public Optional<Post> getPreviousPost(Long memberId, Long postId) {
        List<Post> previousPosts = postRepository.findPreviousPost(memberId, postId);
        return previousPosts.isEmpty() ? Optional.empty() : Optional.of(previousPosts.get(0));
    }


    public Optional<Post> getNextPost(Long memberId, Long postId) {
        List<Post> nextPosts = postRepository.findNextPost(memberId, postId);
        return nextPosts.isEmpty() ? Optional.empty() : Optional.of(nextPosts.get(0));
    }

    //filter = 0 : 최신순, =1 : 인기순
    public List<PostDto> getAllPosts(int filter,String keyword){
        List<Post> allPosts;
        if(keyword!=null){
            allPosts = postRepository.findByTitleContaining(keyword);
        }else {
            if (filter == 0) {
                allPosts = postRepository.findAllByIsTempFalseOrderByCreatedAtDesc();
            } else {
                allPosts = postRepository.findAllByIsTempFalseOrderByLikesSizeDesc();
            }
        }


        return allPosts.stream().map(post -> {
            // 로그 출력
            log.info("Post URL: {}", post.getPostUrl());
            log.info("Service: /Users/iyeji/Desktop/lionJava/blog_project/main_images/"+post.getMainImageUrl());

            return new PostDto(
                    post.getPostId(),
                    post.getTitle(),
                    post.getPostUrl(),
                    post.getIntroduce(),
                    post.getCreatedAt(),
                    post.getCommentList().size(),
                    post.getMember().getName(),
                    post.getLikesList().size(),
                    "/main_images/"+post.getMainImageUrl()
            );
        }).collect(Collectors.toList());
    }

    public List<PostDto> getAllMyPosts(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        List<Post> myPosts = postRepository.findAllByMember(member);
        return myPosts.stream().map(post -> new PostDto(
                    post.getPostId(),
                    post.getTitle(),
                    post.getPostUrl(),
                    post.getIntroduce(),
                    post.getCreatedAt(),
                    post.getCommentList().size(),
                    post.getMember().getName(),
                    post.getLikesList().size(),
                    "/main_images/"+post.getMainImageUrl()
            )).collect(Collectors.toList());
    }

    public List<TempPostDto> getAllTempPosts(){
        List<Post> allByIsTemp = postRepository.findAllByIsTemp(true);
        return allByIsTemp.stream().map(post -> new TempPostDto(
                post.getTitle(),
                post.getIntroduce(),
                post.getCreatedAt(),
                post.getMember().getName()
        )).collect(Collectors.toList());
    }
}
