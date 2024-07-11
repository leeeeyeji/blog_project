package org.example.blog_project.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.member.jwt.JwtGenerator;
import org.example.blog_project.member.jwt.JwtProvider;
import org.example.blog_project.post.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final JwtProvider jwtProvider;
    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }
    @GetMapping("/postform")
    public String postForm(){
        return "/post/createPostForm";
    }

    @GetMapping("/publishForm")
    public String publishForm(@RequestParam("postId") Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "/post/publishForm";
    }

    @PostMapping("/api/posts")
    @ResponseBody
    public ResponseEntity<?> createPost(@RequestHeader(name = "Authorization") String auth,
                                       @RequestPart(name = "postForm") PostForm postForm,
                                       @RequestParam(name = "files",required = false) List<MultipartFile> files){


        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        CreatePostResDto result = postService.createPost(memberId, postForm, files);

        log.info("" + result.getPostId() + result.getIsTemp());
        if (!result.getIsTemp()) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok("temp");
    }

    @PostMapping("/api/posts/publish")
    @ResponseBody
    public ResponseEntity<?> publishPost(@RequestHeader(name = "Authorization") String auth,
                              @RequestPart(name = "publishForm")PublishForm form,
                              @RequestParam(name = "file",required = false) MultipartFile file) {
        try {
            String token = getToken(auth);
            Long memberId = jwtProvider.getMemberIdFromToken(token);
            String url = postService.publishPost(form, file, memberId);
            return ResponseEntity.ok(url);
        } catch (IllegalStateException e) {
            log.error("URL 중복 오류: " + e.getMessage()); // 로그에 오류 메시지 기록
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
        catch (Exception e) {
            log.error("내부 서버 오류: " + e.getMessage(), e); // 로그에 스택 트레이스와 함께 기록
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Internal server error"));
        }
    }

    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> updatePost(@RequestHeader(name = "Authorization") String auth,
                                             @RequestPart(name = "postForm") PostForm form,
                                             @RequestParam(name = "files",required = false) List<MultipartFile> files,
                                             @PathVariable Long postId ){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String result = postService.updatePost(form, postId, files, memberId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> deletePost(@RequestHeader(name = "Authorization") String auth,
                                             @PathVariable Long postId){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String result = postService.deletePost(postId,memberId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/@{loginId}/{encodedTitle}")
    public String getDetailPost(@RequestHeader(name = "Authorization",required = false) String auth,
                                @PathVariable String loginId,
                                @PathVariable String encodedTitle,
                                Model model) throws UnsupportedEncodingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
        model.addAttribute("isLoggedIn", isLoggedIn);
        /*if (auth !=null){
            String token = getToken(auth);
            Long memberId = jwtProvider.getMemberIdFromToken(token);
            model.addAttribute("memberId",memberId);
            log.info("memberId = "+memberId);
        }*/
        if (isLoggedIn) {
            String memberId= authentication.getName();
            model.addAttribute("memberId", memberId);
        }


        // URL 디코딩
        String decodedTitle = URLDecoder.decode(encodedTitle, "UTF-8");
        log.info("decodedTitle: " + decodedTitle);

        // 게시글 상세 정보를 데이터베이스에서 조회
        DetailPostDto post = postService.getDetailPost(loginId, decodedTitle);

        // 모델에 게시글 정보를 추가
        model.addAttribute("post", post);
        //model.addAttribute("memberId", memberId);


        // 게시글 상세 페이지를 반환
        return "post/postDetail"; // 뷰 이름
    }


    @GetMapping("/")
    public  String getAllPosts(@RequestParam(required = false,defaultValue = "0") int filter,Model model){
        //filter = 0 : 최신순, =1 : 인기순
        List<PostDto> allPosts = postService.getAllPosts(filter);
        log.info("Controller : "+allPosts.get(0).getMainImageUrl());
        model.addAttribute("posts", allPosts);
        return "index";
    }
    @GetMapping("/my")
    public String getAllMyPosts(){
        return "post/myPosts";
    }
    @GetMapping("/api/posts/my")
    public ResponseEntity<List<PostDto>> getAllMyPosts(@RequestHeader(name = "Authorization") String auth,
                                                      Model model){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        log.info("Received Authorization header: " + auth);
        List<PostDto> allMyPosts = postService.getAllMyPosts(memberId);
        model.addAttribute("myPosts",allMyPosts);
        return ResponseEntity.ok(allMyPosts);
    }


    @GetMapping("/api/posts/temp")
    public ResponseEntity<List<TempPostDto>> getAllTempPosts(){
        List<TempPostDto> allTempPosts = postService.getAllTempPosts();
        return ResponseEntity.ok(allTempPosts);
    }


}
