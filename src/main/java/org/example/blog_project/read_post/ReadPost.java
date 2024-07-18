package org.example.blog_project.read_post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.blog_project.member.Member;
import org.example.blog_project.post.Post;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ReadPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime readAt;
}
