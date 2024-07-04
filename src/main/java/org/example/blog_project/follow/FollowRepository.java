package org.example.blog_project.follow;

import org.example.blog_project.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Optional<Follow> findFollowByFrom_userAAndTo_user(Member from_user,Member to_user);
    List<Follow> findAllByFrom_user(Member from_user);
}
