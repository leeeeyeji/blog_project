package org.example.blog_project.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class PostDto {
    private String title;
    private String introduce;
    private LocalDate createdAt;
    private Integer commentsAmount;
    private String author;
    private Integer likes;
    private String mainImageUrl;
}
