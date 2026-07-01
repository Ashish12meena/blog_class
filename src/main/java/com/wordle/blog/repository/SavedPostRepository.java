package com.wordle.blog.repository;

import com.wordle.blog.enitity.SavedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<SavedPost> findByUser_IdAndPost_Id(Long userId, Long postId);

    Page<SavedPost> findByUser_Id(Long userId, Pageable pageable);

    long countByUser_Id(Long userId);
}