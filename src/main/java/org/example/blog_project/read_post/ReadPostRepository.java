package org.example.blog_project.read_post;

import org.example.blog_project.member.Member;
import org.example.blog_project.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadPostRepository extends JpaRepository<ReadPost,Long> {
    List<ReadPost> findByMember_memberIdOrderByReadAtDesc(Long memberId);
    Optional<ReadPost> findByPost(Post post);
}
