package org.example.blog_project.read_post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReadPostDto {
    private Long postId;
    private Long memberId;
    private String title;
    private String author;
    private String mainImageUrl;
    private String introduce;
}
