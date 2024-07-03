package org.example.blog_project.comment;

import org.example.blog_project.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPost(Post post);
    void deleteAllByParentCommentId(Long parentCommentId);
}
