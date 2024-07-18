package org.example.blog_project.read_post;

import lombok.RequiredArgsConstructor;
import org.example.blog_project.member.Member;
import org.example.blog_project.member.MemberRepository;
import org.example.blog_project.post.Post;
import org.example.blog_project.post.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadPostService {
    private final ReadPostRepository readPostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public void saveReadPosts(Long postId,Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 게시글"));
        Optional<ReadPost> byMember = readPostRepository.findByPost(post);
        if(byMember.isEmpty()){
            ReadPost readPost = ReadPost.builder()
                    .member(member)
                    .post(post)
                    .build();


            readPostRepository.save(readPost);
            member.getReadPostList()
                    .add(readPost);
            post.getReadPostList()
                    .add(readPost);
        }


    }
    public List<ReadPostDto> getReadPosts(Long memberId){
        List<ReadPost> readPosts = readPostRepository.findByMember_memberIdOrderByReadAtDesc(memberId);
        List<Post> collect = readPosts.stream().map(ReadPost::getPost).collect(Collectors.toList());
        List<ReadPostDto> collect1 = collect.stream().map(post -> new ReadPostDto(
                post.getMember().getMemberId(),
                post.getPostId(),
                post.getTitle(),
                post.getMember().getName(),
                post.getMainImageUrl(),
                post.getIntroduce()
        )).collect(Collectors.toList());
        return collect1;
    }
}
