package com.wordle.blog.service;

import com.wordle.blog.dto.FollowCheckResponseDTO;
import com.wordle.blog.repository.FollowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FollowService {

    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public FollowCheckResponseDTO isFollowing(Long followerId, Long followingId) {
        boolean result = followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId);
        log.info("Follow check: user {} following user {} -> {}", followerId, followingId, result);

        return FollowCheckResponseDTO.builder()
                .followerId(followerId)
                .followingId(followingId)
                .following(result)
                .build();
    }
}