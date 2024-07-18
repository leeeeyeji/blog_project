package org.example.blog_project.config;

import lombok.RequiredArgsConstructor;
import org.example.blog_project.member.jwt.CustomAccessDeniedHandler;
import org.example.blog_project.member.jwt.CustomJwtAuthenticationEntryPoint;
import org.example.blog_project.member.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/static/**",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/manifest.json",
            "/",
            "/api/**",
            "/profile_images/**",
            "/content_images/**",
            "/main_images/**",
            "/login",
            "/register",
            "/info",
            "/postform",
            "/publishForm",
            "/my",
            "/posts/read",
            "/@**"

    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e->{
                    e.authenticationEntryPoint(customJwtAuthenticationEntryPoint);
                    e.accessDeniedHandler(customAccessDeniedHandler);
                });
        http.authorizeHttpRequests(auth->{
            auth.requestMatchers(AUTH_WHITELIST).permitAll();
            auth.requestMatchers(new RegexRequestMatcher("^/@[\\w-]+/.*$", null)).permitAll();
            auth.anyRequest().authenticated();
        }).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
