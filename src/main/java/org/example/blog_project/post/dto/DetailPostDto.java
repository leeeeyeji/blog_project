package org.example.blog_project.post.dto;

import lombok.Builder;
import lombok.Data;
import org.example.blog_project.post_tag.PostTag;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DetailPostDto {
    private String title;
    private String author;
    private LocalDate createdAt;
    private List<PostTag> postTagList;
    private String series;
    private String content;
    private String profileImageUrl;
    private Long prePostId;
    private Long nextPostId;
}
