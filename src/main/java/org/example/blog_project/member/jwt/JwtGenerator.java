package org.example.blog_project.member.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.blog_project.member.jwt.dto.JWToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtGenerator {
    private final Key key;
    private static final Long TOKEN_EXPIRATION_TIME = 60*60*1000L; //한시간
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(7); //일주일

    public JwtGenerator(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //토큰만들기
    public JWToken generateToken(Long memberId){
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now+TOKEN_EXPIRATION_TIME); //토큰 발급으로부터 한시간 뒤

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        return JWToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
