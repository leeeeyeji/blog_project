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
    public String publishPost(PublishForm form, MultipartFile file,Long memberId) {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글"));
        if(!memberId.equals(post.getMember().getMemberId())){
            return "잘못된 사용자";
        }
        post.setTemp(false);

        post.setIntroduce(form.getIntroduce());
        post.setisHide(form.getIsHide());
        post.setSeries(post.getSeries());

        try {
            String encodedInput = URLEncoder.encode(form.getPostUrl(), "UTF-8");
            String url = String.format("/@%s/%s", post.getMember().getLoginId(), encodedInput);
            post.setPostUrl(url);
        } catch (UnsupportedEncodingException e) {
            log.info(e.getMessage());
        }

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
        Optional<Post> previousPost = getPreviousPost(post.getMember().getMemberId(), post.getPostId());
        Optional<Post> nextPost = getNextPost(post.getMember().getMemberId(), post.getPostId());
        return DetailPostDto.builder()
                .title(post.getTitle())
                .author(post.getMember().getName())
                .createdAt(post.getCreatedAt())
                .postTagList(post.postTagList)
                .series(post.getSeries())
                .content(post.getContent())
                .profileImageUrl(post.getMainImageUrl())
                .prePostUrl(previousPost.map(Post::getPostUrl).orElse(null))
                .nextPostUrl(nextPost.map(Post::getPostUrl).orElse(null))
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

    //TODO 모든 게시글 조회
    public List<Post> getAllPosts(){
        List<Post> allPosts = postRepository.findAll();
        return null;
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
