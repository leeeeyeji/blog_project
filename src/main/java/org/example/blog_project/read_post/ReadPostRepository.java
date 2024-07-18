package org.example.blog_project.read_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadPostRepository extends JpaRepository<ReadPost,Long> {
    List<ReadPost> findByMember_memberIdOrderByReadAtDesc(Long memberId);
}
