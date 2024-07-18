package org.example.blog_project.read_post;

import lombok.RequiredArgsConstructor;
import org.example.blog_project.member.jwt.JwtProvider;
import org.example.blog_project.post.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReadPostController {
    private final ReadPostService readPostService;
    private final JwtProvider jwtProvider;

    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }

    @GetMapping("/api/posts/read")
    public ResponseEntity<List<Post>> getReadPosts(@RequestHeader(name = "Authorization") String auth,
                                                   Model model){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);

        List<Post> readPosts = readPostService.getReadPosts(memberId);
        model.addAttribute("read_posts",readPosts);
        return ResponseEntity.ok(readPosts);
    }

}
