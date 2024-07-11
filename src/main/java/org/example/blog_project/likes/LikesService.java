package org.example.blog_project.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.likes.dto.LikesDto;
import org.example.blog_project.member.Member;
import org.example.blog_project.member.MemberRepository;
import org.example.blog_project.post.Post;
import org.example.blog_project.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {
    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public String likes(Long postId,Long memberId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 게시물"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        if(likesRepository.findByMemberAndPost(member,post).isPresent()){
            // 좋아요 취소
            Likes likes = likesRepository.findByMemberAndPost(member, post)
                    .orElseThrow(() -> new RuntimeException("존재하지않는 좋아요"));
            likesRepository.delete(likes);
            return "좋아요 취소 성공";
        }
        //좋아요 등록
        Likes like = Likes.builder()
                .post(post)
                .member(member)
                .build();
        likesRepository.save(like);
        return "좋아요 등록 성공";
    }
    public List<LikesDto> getAllLikes(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        List<Likes> allLikes = likesRepository.findAllByMember(member);

        return allLikes.stream().map(likes -> new LikesDto(
                likes.getPost().getTitle(),
                likes.getPost().getIntroduce(),
                likes.getPost().getCreatedAt(),
                likes.getPost().getCommentList().size(),
                likes.getPost().getMember().getName(),
                likes.getPost().getLikesList().size(),
                likes.getPost().getMainImageUrl()
        )).collect(Collectors.toList());
    }

}
