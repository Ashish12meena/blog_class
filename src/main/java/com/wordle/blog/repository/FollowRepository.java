package com.wordle.blog.repository;

import com.wordle.blog.enitity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    Optional<Follow> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    @Query("select f from Follow f where f.follower.id = :userId")
    Page<Follow> findAllFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query("select f from Follow f where f.following.id = :userId")
    Page<Follow> findAllFollowers(@Param("userId") Long userId, Pageable pageable);

    long countByFollower_Id(Long userId);

    long countByFollowing_Id(Long userId);
}