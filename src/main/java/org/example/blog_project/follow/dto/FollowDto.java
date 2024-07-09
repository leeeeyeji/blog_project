package org.example.blog_project.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FollowDto {
    private Long memberId;
    private String name;
    private String loginId;

}
