package com.wordle.blog.repository;

import com.wordle.blog.enitity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByLikedBy_IdAndPost_Id(Long userId, Long postId);

    Optional<Like> findByLikedBy_IdAndPost_Id(Long userId, Long postId);

    long countByPost_Id(Long postId);

    @Query("""
            select l.post.id as postId, l.post.title as title, count(l.id) as likeCount
            from Like l
            where l.likedBy.id = :userId
            group by l.post.id, l.post.title
            order by count(l.id) desc
            """)
    List<MostLikedProjection> findMostLikedPostsByUser(@Param("userId") Long userId);

    interface MostLikedProjection {
        Long getPostId();
        String getTitle();
        Long getLikeCount();
    }
}