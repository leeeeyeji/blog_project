package org.example.blog_project.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.example.blog_project.member.dto.*;
import org.example.blog_project.member.jwt.JwtGenerator;
import org.example.blog_project.member.jwt.JwtProvider;
import org.example.blog_project.member.jwt.dto.JWToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtGenerator jwtGenerator;
    private final JwtProvider jwtProvider;

    private static String getToken(String auth){
        if (auth == null || !auth.startsWith("Bearer ")){
            throw new RuntimeException("잘못된 토큰");
        }
        return auth.substring(7);
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("registerForm")RegisterForm form){
        return "login/registerForm";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")LoginForm form){
        return "login/loginForm";
    }

    @GetMapping("/info")
    public String userInfo(){
        return "login/userInfo";
    }
    @GetMapping("/api/user-info")
    public ResponseEntity<InfoDto> userInfo(@RequestHeader(name = "Authorization") String auth){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        InfoDto userInfo = memberService.getUserInfo(memberId);
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/api/info/blog-name")
    @ResponseBody
    public void updateBlogName(@RequestHeader(name = "Authorization") String auth,
                               @RequestParam String blogName){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        memberService.updateBlogName(blogName,memberId);
    }

    @PutMapping("/api/info/profile-image")
    @ResponseBody
    public String updateProfileImage(@RequestHeader(name = "Authorization") String auth,
                                     @RequestParam(name = "profileImage")MultipartFile file){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        return "/profile_images/" + memberService.updateProfileImage(file,memberId);
    }

    @DeleteMapping("/api/info/profile-image")
    public ResponseEntity<String> deleteProfileImage(@RequestHeader(name = "Authorization") String auth,
                                                     @RequestParam("fileName") String fileName) {
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        boolean deleted = memberService.deleteFile(memberId, fileName);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile image not found");
        }
    }

    @GetMapping("/api/checkLoginId")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String loginId) {
        Boolean isAvailable = memberService.idCheck(loginId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/api/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        Boolean isAvailable = memberService.emailCheck(email);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/api/register")
    public String register(@Valid @ModelAttribute RegisterForm form,
                           BindingResult bindingResult,
                           HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return "login/registerForm";
        }

        Boolean result = memberService.register(form);
        if(result){
            return "redirect:/login";
        }else {
            bindingResult.reject("registerFail","회원가입 중 문제가 생겼습니다");
            return "login/registerForm";
        }

    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm form,
                        BindingResult bindingResult,
                        HttpServletResponse response,
                        @RequestParam(defaultValue = "/") String redirectURL) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Validation failed.\"}");
        }

        Long memberId = memberService.login(form);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Invalid credentials.\"}");
        }

//        HttpSession session = request.getSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
//
//        UserContext.setUserId(String.valueOf(memberId));

        //시큐리티
        JWToken jwToken = jwtGenerator.generateToken(memberId);
        Cookie cookie = new Cookie("jwtToken",jwToken.getAccessToken().trim());
        cookie.setMaxAge(3600); //쿠키 만료시간
        cookie.setHttpOnly(false); //자바스크립트를 통한 접근 허용
        cookie.setPath("/"); //모든경로 쿠키사용
        response.addCookie(cookie);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectURL));

        return ResponseEntity.ok().body("{\"redirectURL\":\"" + redirectURL + "\"}");
    }


    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 삭제
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie); // 변경된 쿠키 정보를 응답에 추가


        return ResponseEntity.ok().body("{\"message\":\"Logged out successfully.\"}");
    }


    @PutMapping("/api/info/send-comment-email")
    public ResponseEntity<Void> updateAllowCommentEmail(@RequestHeader(name = "Authorization") String auth,
                                                        @RequestBody AllowCommentDto allowCommentDto){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        memberService.updateAllowCommentEmail(memberId,allowCommentDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/info/send-update-email")
    public ResponseEntity<Void> updateAllowUpdateEmail(@RequestHeader(name = "Authorization") String auth,
                                                       @RequestBody AllowUpdateDto allowUpdateDto){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        memberService.updateAllowUpdateEmail(memberId,allowUpdateDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/info/email")
    public ResponseEntity<String> updateEmail(@RequestHeader(name = "Authorization") String auth,
                                              @RequestParam String newEmail){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Boolean result = memberService.updateEmail(memberId, newEmail);
        if(result){
            return ResponseEntity.ok("이메일 변경 완료");
        }
        return ResponseEntity.ok("이메일 변경 오류");

    }


    @DeleteMapping("/api/delete-account")
    public ResponseEntity<String> deleteMember(@RequestHeader(name = "Authorization") String auth,
                                               @RequestParam String password){
        String token = getToken(auth);
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Boolean result = memberService.deleteMember(memberId, password);
        if (result){
            return ResponseEntity.ok("회원 탈퇴 완료");
        }
        return ResponseEntity.ok("오류");
    }
}
