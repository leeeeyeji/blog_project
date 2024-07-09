package org.example.blog_project.post.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class PostDto {
    private Long postId;
    private String title;
    private String postUrl;
    private String introduce;
    private LocalDate createdAt;
    private Integer commentsAmount;
    private String author;
    private Integer likes;
    private String mainImageUrl;
}
