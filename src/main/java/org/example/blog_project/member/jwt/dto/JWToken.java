package org.example.blog_project.member.jwt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JWToken {
    public String grantType;
    public String accessToken;
    public String refreshToken;
}
