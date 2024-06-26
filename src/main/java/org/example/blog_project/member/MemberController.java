package org.example.blog_project.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.example.blog_project.member.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("registerForm")RegisterForm form){
        return "login/registerForm";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")LoginForm form){
        return "login/loginForm";
    }

    @GetMapping("/info")
    public String userInfo(Model model){
        Long memberId = Long.parseLong(UserContext.getUserId());
        InfoDto userInfo = memberService.getUserInfo(memberId);
        model.addAttribute("userInfo", userInfo);
        return "login/userInfo";
    }

    @PutMapping("/api/info/blog-name")
    @ResponseBody
    public void updateBlogName(@RequestParam String blogName){
        Long memberId =Long.parseLong(UserContext.getUserId());
        memberService.updateBlogName(blogName,memberId);
    }

    @PutMapping("/api/info/profile-image")
    @ResponseBody
    public String updateProfileImage(@RequestParam(name = "profileImage")MultipartFile file){
        Long memberId =Long.parseLong(UserContext.getUserId());
        return "/profile_images/" + memberService.updateProfileImage(file,memberId);
    }

    @DeleteMapping("/api/info/profile-image")
    public ResponseEntity<String> deleteProfileImage(@RequestParam("fileName") String fileName) {
        Long memberId = Long.parseLong(UserContext.getUserId());
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
    public String login(@Valid @ModelAttribute LoginForm form,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        @RequestParam(defaultValue = "/") String redirectURL) {

        if (bindingResult.hasErrors()) {
            return "redirect:/login";
        }

        Long memberId = memberService.login(form);
        if (memberId == null) {
            return "redirect:/login?error=true";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);

        UserContext.setUserId(String.valueOf(memberId));

        return "redirect:" + redirectURL;
    }



    @PutMapping("/api/info/send-comment-email")
    public ResponseEntity<Void> updateAllowCommentEmail(@RequestBody AllowCommentDto allowCommentDto){
        Long memberId = Long.parseLong(UserContext.getUserId());
        memberService.updateAllowCommentEmail(memberId,allowCommentDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/info/send-update-email")
    public ResponseEntity<Void> updateAllowUpdateEmail(@RequestBody AllowUpdateDto allowUpdateDto){
        Long memberId = Long.parseLong(UserContext.getUserId());
        memberService.updateAllowUpdateEmail(memberId,allowUpdateDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/info/email")
    public ResponseEntity<String> updateEmail(@RequestParam String newEmail){
        Long memberId = Long.parseLong(UserContext.getUserId());
        Boolean result = memberService.updateEmail(memberId, newEmail);
        if(result){
            return ResponseEntity.ok("이메일 변경 완료");
        }
        return ResponseEntity.ok("이메일 변경 오류");

    }


    @DeleteMapping("/api/delete-account")
    public ResponseEntity<String> deleteMember(@RequestParam String password){
        Long memberId =Long.parseLong(UserContext.getUserId());
        Boolean result = memberService.deleteMember(memberId, password);
        if (result){
            return ResponseEntity.ok("회원 탈퇴 완료");
        }
        return ResponseEntity.ok("오류");
    }
}
