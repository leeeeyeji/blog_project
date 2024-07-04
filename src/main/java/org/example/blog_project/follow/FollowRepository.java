package org.example.blog_project.follow;

import org.example.blog_project.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Optional<Follow> findFollowByFromUserAndToUser(Member fromUser,Member toUser);
    List<Follow> findAllByFromUser(Member from_user);
}
