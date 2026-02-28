package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId")
    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);

    int countByUserId(Long userId);
}
