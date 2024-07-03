package org.example.blog_project.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.member.UserContext;
import org.example.blog_project.member.jwt.JwtGenerator;
import org.example.blog_project.member.jwt.JwtProvider;
import org.example.blog_project.post.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;



@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final JwtGenerator jwtGenerator;
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
    public CreatePostResDto createPost(@RequestHeader(name = "Authorization") String auth,
                                       @RequestPart(name = "postForm") PostForm postForm,
                                       @RequestParam(name = "files",required = false) List<MultipartFile> files){


        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        CreatePostResDto result = postService.createPost(memberId, postForm, files);

        log.info("" + result.getPostId() + result.getIsTemp());
        if (!result.getIsTemp()) {
            return result;
        }

        return null;
    }

    @PostMapping("/api/posts/publish")
    @ResponseBody
    public String publishPost(@RequestHeader(name = "Authorization") String auth,
                              @RequestPart(name = "publishForm")PublishForm form,
                              @RequestParam(name = "file",required = false) MultipartFile file) {
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String url = postService.publishPost(form, file,memberId);
        return url;
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
    public String getDetailPost(@PathVariable String loginId,
                                @PathVariable String encodedTitle,
                                Model model) throws UnsupportedEncodingException {

        // URL 디코딩
        String decodedTitle = URLDecoder.decode(encodedTitle, "UTF-8");
        log.info("decodedTitle: " + decodedTitle);

        // 게시글 상세 정보를 데이터베이스에서 조회
        DetailPostDto post = postService.getDetailPost(loginId, decodedTitle);

        // 모델에 게시글 정보를 추가
        model.addAttribute("post", post);

        // 게시글 상세 페이지를 반환
        return "post/postDetail"; // 뷰 이름
    }

    @GetMapping("/api/posts?filter={filter}")
    public  ResponseEntity<List<PostDto>> getAllPosts(@RequestParam int filter){

        //TODO 모든 게시물 조회

        return null;
    }

    @GetMapping("/api/posts/temp")
    public ResponseEntity<List<TempPostDto>> getAllTempPosts(){
        List<TempPostDto> allTempPosts = postService.getAllTempPosts();
        return ResponseEntity.ok(allTempPosts);
    }


}
