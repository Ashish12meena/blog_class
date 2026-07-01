package com.wordle.blog.repository;

import com.wordle.blog.enitity.Post;
import com.wordle.blog.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.id = :id and p.deletedAt is null")
    Optional<Post> findActiveById(@Param("id") Long id);

    @Query("select p from Post p where p.createdBy.id = :userId and p.status = :status and p.deletedAt is null")
    Page<Post> findByCreatedBy_IdAndStatus(@Param("userId") Long userId, @Param("status") PostStatus status, Pageable pageable);

    @Query("select p from Post p where p.createdBy.id = :userId and p.deletedAt is null")
    Page<Post> findByCreatedBy_Id(@Param("userId") Long userId, Pageable pageable);

    @Query("select p from Post p where p.status = :status and p.deletedAt is null")
    Page<Post> findByStatus(@Param("status") PostStatus status, Pageable pageable);

    @Query("select count(p) from Post p where p.createdBy.id = :userId and p.deletedAt is null")
    long countByCreatedBy_Id(@Param("userId") Long userId);

    @Query("""
            select distinct p from Post p
            join PostCategory pc on pc.post.id = p.id
            where pc.category.id = :categoryId and p.deletedAt is null
            """)
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount + 1 where p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("update Post p set p.commentCount = case when p.commentCount > 0 then p.commentCount - 1 else 0 end where p.id = :postId")
    void decrementCommentCount(@Param("postId") Long postId);
}