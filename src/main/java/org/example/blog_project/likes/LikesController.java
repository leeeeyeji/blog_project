package org.example.blog_project.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.likes.dto.LikesDto;
import org.example.blog_project.member.jwt.JwtGenerator;
import org.example.blog_project.member.jwt.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikesController {
    private final LikesService likesService;
    private final JwtProvider jwtProvider;

    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> insertLikes(@RequestHeader(name = "Authorization") String auth,
                                              @PathVariable Long postId){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        try {
            likesService.insertLikes(postId,memberId);
            return ResponseEntity.ok("좋아요 등록 완료");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> undoLikes(@RequestHeader(name = "Authorization") String auth,
                                            @PathVariable Long postId){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        likesService.undoLikes(postId,memberId);
        return ResponseEntity.ok("좋아요 취소 완료");
    }

    @GetMapping
    public ResponseEntity<List<LikesDto>> getAllLikes(@RequestHeader(name = "Authorization") String auth){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        List<LikesDto> allLikes = likesService.getAllLikes(memberId);
        return ResponseEntity.ok(allLikes);
    }
}
