package org.example.blog_project.member.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(@NonNull ServletRequest request,
                         @NonNull ServletResponse response,
                         @NonNull FilterChain chain) throws IOException, ServletException {
        try {
            String token = resolveToken((HttpServletRequest)request);
            if (token !=null && jwtProvider.validateToken(token)){
                //유효한 토큰
                Authentication authentication = jwtProvider.getAuthentication(token);

                //스프링시큐리티의 인증 컨텍스트에 설정
                //현재 스레드의 보안컨텍스트에 authentication을 설정해 이후의 요청 처리과정에서 현재사용자가 인증된 사용자로 간주됨
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Authenticated user: {}", authentication.getName());
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }
        chain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }


}
