package org.example.blog_project.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishForm {
    private Long postId;
    private String introduce;
    private Boolean isHide;
    private String series;
    private String postUrl;
}
