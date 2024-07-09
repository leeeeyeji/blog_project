package org.example.blog_project.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.follow.dto.FollowDto;
import org.example.blog_project.member.Member;
import org.example.blog_project.member.MemberRepository;
import org.example.blog_project.post.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    //follow를 하는 나의 memberId = from_userMemberId , 팔로우 할 사람의 memberId = to_userMemberId
    public String follow(Long to_userMemberId,Long from_userMemberId){

        Member from_user = memberRepository.findById(from_userMemberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        Member to_user = memberRepository.findById(to_userMemberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));

        if(to_userMemberId.equals(from_userMemberId)){
            throw new RuntimeException("자기자신 팔로우 불가");
        }
        //중복 팔로우 불가
        if (followRepository.findFollowByFromUserAndToUser(from_user,to_user).isPresent()){
            throw new RuntimeException("이미 팔로우한 사용자");
        }

        Follow follow = Follow.builder()
                .fromUser(from_user)
                .toUser(to_user)
                .build();

        followRepository.save(follow);
        return "팔로우 성공";
    }

    public String unFollow(Long to_userMemberId,Long from_userMemberId){
        Member from_user = memberRepository.findById(from_userMemberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        Member to_user = memberRepository.findById(to_userMemberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        log.info("user = "+to_userMemberId+from_userMemberId);
        Follow follow = followRepository.findFollowByFromUserAndToUser(from_user, to_user)
                .orElseThrow(() -> new RuntimeException("존재하지않는 팔로우"));
        followRepository.delete(follow);
        return "팔로우 삭제 성공";
    }

    public List<FollowDto> getAllFollows(Long from_userMemberId){
        Member from_user = memberRepository.findById(from_userMemberId)
                .orElseThrow(() -> new RuntimeException("존재하지않는 사용자"));
        List<Follow> allByFromUser = followRepository.findAllByFromUser(from_user);

        return allByFromUser.stream().map(follow -> new FollowDto(
                follow.getToUser().getMemberId(),
                follow.getToUser().getName(),
                follow.getToUser().getLoginId()
        )).collect(Collectors.toList());
    }
}
