package org.example.blog_project.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.follow.dto.FollowDto;
import org.example.blog_project.member.jwt.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final JwtProvider jwtProvider;

    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }

    @PostMapping("/api/follows")
    public ResponseEntity<String> follow(@RequestHeader(name = "Authorization") String auth,
                                         @RequestParam Long to_userMemberId){
        String token = getToken(auth);
        Long from_userMemberId = jwtProvider.getMemberIdFromToken(token);
        String result = followService.follow(to_userMemberId, from_userMemberId);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/api/follows/{userId}")
    public ResponseEntity<String> unFollow(@RequestHeader(name = "Authorization") String auth,
                                           @PathVariable(name = "userId") Long to_userMemberId){
        String token = getToken(auth);
        Long from_userMemberId = jwtProvider.getMemberIdFromToken(token);
        String result = followService.unFollow(to_userMemberId, from_userMemberId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/api/follows")
    public ResponseEntity<List<FollowDto>> getAllFollows(@RequestHeader(name = "Authorization") String auth){
        String token = getToken(auth);
        Long from_userMemberId = jwtProvider.getMemberIdFromToken(token);
        List<FollowDto> allFollows = followService.getAllFollows(from_userMemberId);
        return ResponseEntity.ok(allFollows);
    }
}
