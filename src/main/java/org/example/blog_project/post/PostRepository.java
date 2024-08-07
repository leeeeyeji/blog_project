package org.example.blog_project.post;


import org.example.blog_project.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p FROM Post p WHERE p.member.memberId = :memberId AND p.postId < :postId ORDER BY p.postId DESC")
    List<Post> findPreviousPost(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.member.memberId = :memberId AND p.postId > :postId ORDER BY p.postId ASC")
    List<Post> findNextPost(@Param("memberId") Long memberId, @Param("postId") Long postId);

    Optional<Post> findByPostUrl(String url);

    List<Post> findAllByIsTemp(boolean isTemp);

    @Query("SELECT p FROM Post p WHERE p.isTemp = false ORDER BY p.createdAt DESC")
    List<Post> findAllByIsTempFalseOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p WHERE p.isTemp = false ORDER BY SIZE(p.likesList) DESC")
    List<Post> findAllByIsTempFalseOrderByLikesSizeDesc();

    List<Post> findAllByMember(Member member);

    List<Post> findByTitleContaining(String keyword);

}
