package com.wordle.blog.service;

import com.wordle.blog.dto.*;
import com.wordle.blog.enitity.Follow;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.AlreadyFollowingException;
import com.wordle.blog.exception.CannotFollowSelfException;
import com.wordle.blog.exception.FollowNotFoundException;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.FollowMapper;
import com.wordle.blog.repository.FollowRepository;
import com.wordle.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;

    public FollowService(FollowRepository followRepository,
            UserRepository userRepository,
            FollowMapper followMapper) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.followMapper = followMapper;
    }

    @Transactional
    public FollowResponseDTO follow(FollowRequestDto request) {
        if (request.getFollowerId().equals(request.getFollowingId())) {
            throw new CannotFollowSelfException("A user cannot follow themselves");
        }

        if (followRepository.existsByFollower_IdAndFollowing_Id(request.getFollowerId(), request.getFollowingId())) {
            log.warn("User {} attempted to follow user {} twice", request.getFollowerId(), request.getFollowingId());
            throw new AlreadyFollowingException("Already following this user");
        }

        User follower = userRepository.findById(request.getFollowerId())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getFollowerId()));

        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getFollowingId()));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        Follow saved = followRepository.save(follow);
        log.info("User {} followed user {}", request.getFollowerId(), request.getFollowingId());

        return followMapper.toResponseDTO(saved);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollower_IdAndFollowing_Id(followerId, followingId)
                .orElseThrow(() -> {
                    log.warn("Unfollow attempted but no follow found: follower {} following {}", followerId, followingId);
                    return new FollowNotFoundException("Follow relationship not found");
                });

        followRepository.delete(follow);
        log.info("User {} unfollowed user {}", followerId, followingId);
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

    // Accounts that `userId` follows
    public Page<FollowUserSummaryDTO> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findAllFollowing(userId, pageable)
                .map(followMapper::toFollowingSummary);
    }

    // Accounts that follow `userId`
    public Page<FollowUserSummaryDTO> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findAllFollowers(userId, pageable)
                .map(followMapper::toFollowerSummary);
    }

    public FollowCountResponseDTO getFollowCounts(Long userId) {
        long followerCount = followRepository.countByFollowing_Id(userId);
        long followingCount = followRepository.countByFollower_Id(userId);

        return FollowCountResponseDTO.builder()
                .userId(userId)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }
}