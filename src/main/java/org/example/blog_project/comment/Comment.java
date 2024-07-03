package org.example.blog_project.comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.blog_project.member.Member;
import org.example.blog_project.post.Post;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @NotNull
    private String content;

    @NotNull
    private LocalDate createdAt = LocalDate.now();

    private Long parentCommentId = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String content, Long parentCommentId, Member member, Post post) {
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.member = member;
        this.post = post;
    }

    public void updateContent(String content){
        this.content = content;
    }
}
