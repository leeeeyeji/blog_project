package org.example.blog_project.likes;

import org.example.blog_project.member.Member;
import org.example.blog_project.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByMemberAndPost(Member member, Post post);
    List<Likes> findAllByMember(Member member);
}
