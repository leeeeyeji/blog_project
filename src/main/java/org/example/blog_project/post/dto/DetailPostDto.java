package org.example.blog_project.post.dto;

import lombok.Builder;
import lombok.Data;
import org.example.blog_project.comment.Comment;
import org.example.blog_project.likes.Likes;
import org.example.blog_project.member.Member;
import org.example.blog_project.post_image.PostImage;
import org.example.blog_project.post_tag.PostTag;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DetailPostDto {
    private Long postId;
    private String title;
    private String author;
    private LocalDate createdAt;
    private List<PostTag> postTagList;
    private String series;
    private String content;
    private String profileImageUrl;
    private String prePostUrl;
    private String nextPostUrl;
    private List<PostImage> postImageList;
    private List<Likes> likesList;
    private List<Comment> commentList;
}
