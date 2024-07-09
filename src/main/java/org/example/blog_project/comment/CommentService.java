package org.example.blog_project.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.comment.dto.CommentDto;
import org.example.blog_project.comment.dto.CommentForm;
import org.example.blog_project.member.Member;
import org.example.blog_project.member.MemberRepository;

import org.example.blog_project.post.Post;
import org.example.blog_project.post.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public String createComment(CommentForm commentForm,Long postId,Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물"));

        Comment comment = Comment.builder()
                .content(commentForm.getContent())
                .parentCommentId(commentForm.getParentCommentId())
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);
        member.getCommentList()
                .add(comment);
        post.getCommentList()
                .add(comment);
        return "댓글 저장 완료";
    }

    @Transactional
    public String updateComment(Long commentId,CommentForm commentForm,Long memberId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 댓글"));
        if(!memberId.equals(comment.getMember().getMemberId())){
            return "잘못된사용자";
        }

        comment.updateContent(commentForm.getContent());

        commentRepository.save(comment);
        return "댓글 수정 완료";
    }

    @Transactional
    public String deleteComment(Long commentId,Long memberId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 댓글"));
        if(!memberId.equals(comment.getMember().getMemberId())){
            return "잘못된 사용자";
        }

        commentRepository.deleteAllByParentCommentId(comment.getCommentId());
        commentRepository.delete(comment);

        return "댓글 삭제 완료";
    }

    public List<CommentDto> getAllComments(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 게시물"));
        List<Comment> allComments = commentRepository.findAllByPost(post);

        return allComments.stream().map(comment -> new CommentDto(
                comment.getMember().getName(),
                comment.getMember().getProfileImageUrl(),
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getParentCommentId()
        )).collect(Collectors.toList());
    }
}
