package org.example.blog_project.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class TempPostDto {
    private String title;
    private String introduce;
    private LocalDate createdAt;
    private String author;
}
