package org.example.blog_project.member;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.member.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    @Value("${file.path}")
    private String uploadDir;


    private final MemberRepository memberRepository;

    @Transactional
    public Long login(LoginForm form){
        Member member = memberRepository.findMemberByLoginId(form.getLoginId());

        if(member == null){
            return null;
        }

        if(!member.getPassword().equals(form.getPassword())){
            return null;
        }

        return member.getMemberId();
    }

    @Transactional
    public Boolean register(RegisterForm form) {
        // 아이디와 이메일 형식 검증
        if (!form.getLoginId().matches("^[a-zA-Z0-9]{8,}$")) {
            return false;
        }
        if (!form.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }
        if (!idCheck(form.getLoginId()) || !emailCheck(form.getEmail())) {
            return false;
        }

        Member member = Member.builder()
                .loginId(form.getLoginId())
                .password(form.getPassword())
                .name(form.getName())
                .email(form.getEmail())
                .blogName(form.getLoginId())
                .allowCommentPush(form.getAllowCommentPush())
                .allowUpdatePush((form.getAllowUpdatePush()))
                .build();

        memberRepository.save(member);
        return true;
    }



    public Boolean idCheck(String loginId){
        Member result = memberRepository.findMemberByLoginId(loginId);
        if(result == null){
            return true;
        }
        return false;
    }

    public Boolean emailCheck(String email){
        Member result = memberRepository.findMemberByEmail(email);
        if(result == null){
            return true;
        }
        return false;
    }

    public InfoDto getUserInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 유저"));

        log.info(member.getProfileImageUrl());
        return InfoDto.builder()
                .blogName(member.getBlogName())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .email(member.getEmail())
                .allowCommentPush(member.getAllowCommentPush())
                .allowUpdatePush(member.getAllowUpdatePush())
                .build();
    }

    @Transactional
    public void updateBlogName(String blogName,Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 유저"));
        member.setBlogName(blogName);

        memberRepository.save(member);

    }

    @Transactional
    public String updateProfileImage(MultipartFile file, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));

        deleteFile(memberId, member.getProfileImageUrl());
        String fileName = saveFile(file);
        member.setProfileImageUrl(fileName);

        memberRepository.save(member);

        return fileName;
    }

    @Transactional
    public boolean deleteFile(Long memberId, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));

            if (member.getProfileImageUrl() == null) {
                return false;
            }

            member.setProfileImageUrl(null);
            memberRepository.save(member);
            Path path = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(path);
            System.out.println("File deleted: " + path.toString());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
            return false;
        }
    }


    private String saveFile(MultipartFile file) {
        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save the file
            file.transferTo(filePath.toFile());

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Transactional
    public void updateAllowCommentEmail(Long memberId, AllowCommentDto allowCommentDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
        member.setAllowCommentPush(allowCommentDto.getValue());
        memberRepository.save(member);
    }
    @Transactional
    public void updateAllowUpdateEmail(Long memberId, AllowUpdateDto allowUpdateDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
        member.setAllowUpdatePush(allowUpdateDto.getValue());
        memberRepository.save(member);
    }

    @Transactional
    public Boolean updateEmail(Long memberId,String newEmail){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
        if (!newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }
        if (!emailCheck(newEmail)) {
            return false;
        }
        member.setEmail(newEmail);
        memberRepository.save(member);
        return true;
    }

    @Transactional
    public Boolean deleteMember(Long memberId,String password){
        Member member = memberRepository.findById(memberId)
                .orElse(null);

        if (member == null || !(member.getPassword().equals(password))){
            return false;
        }
        memberRepository.delete(member);
        return true;
    }
}
