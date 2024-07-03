package org.example.blog_project.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private String author;
    private String profileImageUrl;
    private LocalDate createdAt;
    private String content;
    private Long parentCommentId;
}
