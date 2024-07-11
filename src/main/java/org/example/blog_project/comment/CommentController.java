package org.example.blog_project.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.comment.dto.CommentDto;
import org.example.blog_project.comment.dto.CommentForm;
import org.example.blog_project.member.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@RequestHeader(name = "Authorization") String auth,
                                                @RequestBody CommentForm commentForm,
                                                @PathVariable Long postId){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        try {
            String result = commentService.createComment(commentForm, postId,memberId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@RequestHeader(name = "Authorization") String auth,
                                                @PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @RequestBody CommentForm commentForm,
                                                Model model){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String result = commentService.updateComment(commentId, commentForm,memberId);
        model.addAttribute("memberId",memberId);

        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@RequestHeader(name = "Authorization") String auth,
                                                @PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                Model model){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String result = commentService.deleteComment(commentId,memberId);
        model.addAttribute("memberId",memberId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(@RequestHeader(name = "Authorization") String auth,
                                                           @PathVariable Long postId){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        List<CommentDto> allComments = commentService.getAllComments(postId);
        return ResponseEntity.ok(allComments);
    }
}
