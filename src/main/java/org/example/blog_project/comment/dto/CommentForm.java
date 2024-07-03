package org.example.blog_project.comment.dto;

import lombok.Data;

@Data
public class CommentForm {
    private String content;
    private Long parentCommentId;
}
