package org.example.blog_project.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FollowDto {
    private String name;
    private Long loginId;

}
