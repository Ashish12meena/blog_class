package com.wordle.blog.repository;

import com.wordle.blog.enitity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // top-level comments only (no parent) for a given post
    @Query("select c from Comment c where c.post.id = :postId and c.parentComment is null order by c.createdAt desc")
    Page<Comment> findTopLevelByPostId(@Param("postId") Long postId, Pageable pageable);

    // replies for a given parent comment
    Page<Comment> findByParentComment_IdOrderByCreatedAtAsc(Long parentCommentId, Pageable pageable);

    long countByPost_Id(Long postId);

    long countByParentComment_Id(Long parentCommentId);
}