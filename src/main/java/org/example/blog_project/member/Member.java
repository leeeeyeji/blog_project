package org.example.blog_project.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blog_project.comment.Comment;
import org.example.blog_project.follow.Follow;
import org.example.blog_project.likes.Likes;
import org.example.blog_project.post.Post;
import org.example.blog_project.read_post.ReadPost;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @NotNull
    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String email;

    private String profileImageUrl;

    @Column(nullable = false)
    private LocalDate registrationDate = LocalDate.now();

    @Column(nullable = false)
    private Boolean isAdmin = false;

    @Column(nullable = false) //DB에 넣을때만 null이 아니면 됨
    private String blogName = loginId;

    @NotNull //입력할때 null X
    private Boolean allowCommentPush = true;

    @NotNull
    private Boolean allowUpdatePush = true;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "from_user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Follow> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "to_user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReadPost> readPostList = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, String email, String blogName, Boolean allowCommentPush, Boolean allowUpdatePush) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.blogName = blogName;
        this.allowCommentPush = allowCommentPush;
        this.allowUpdatePush = allowUpdatePush;
    }
}
