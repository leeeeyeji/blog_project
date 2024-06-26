package org.example.blog_project.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.blog_project.comment.Comment;
import org.example.blog_project.likes.Likes;
import org.example.blog_project.member.Member;
import org.example.blog_project.post_image.PostImage;
import org.example.blog_project.post_tag.PostTag;
import org.example.blog_project.read_post.ReadPost;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String series = null;

    @NotNull
    private Boolean isTemp = true;

    @NotNull
    private Boolean isHide = false;

    @NotNull
    private LocalDate createdAt = LocalDate.now();

    private String mainImageUrl = null;

    @NotNull
    private Integer views = 0;

    private String introduce;


    private String postUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "orderIndex")
    List<PostImage> postImageList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReadPost> readPostList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PostTag> postTagList = new ArrayList<>();


    @Builder
    public Post(String title, String content, Boolean isTemp, Member member) {
        this.title = title;
        this.content = content;
        this.isTemp = isTemp;
        this.member = member;
    }

    public void setTemp(Boolean temp) {
        isTemp = temp;
    }

    public void updateTitle(String title){
        this.title = title;
    }
    public void updateContent(String content){
        this.content = content;
    }
    public void setIntroduce(String introduce){
        this.introduce = introduce;
    }
    public void setSeries(String series){
        this.series = series;
    }

    public void setisHide(Boolean hide) {
        isHide = hide;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }
}
