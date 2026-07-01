package com.wordle.blog.mapper;

import com.wordle.blog.dto.FollowResponseDTO;
import com.wordle.blog.dto.FollowUserSummaryDTO;
import com.wordle.blog.enitity.Follow;
import org.springframework.stereotype.Component;

@Component
public class FollowMapper {

    public FollowResponseDTO toResponseDTO(Follow follow) {
        return FollowResponseDTO.builder()
                .id(follow.getId())
                .followerId(follow.getFollower().getId())
                .followingId(follow.getFollowing().getId())
                .createdAt(follow.getCreatedAt())
                .build();
    }

    // Used for "who follows me" lists -> show the follower's profile
    public FollowUserSummaryDTO toFollowerSummary(Follow follow) {
        return FollowUserSummaryDTO.builder()
                .userId(follow.getFollower().getId())
                .username(follow.getFollower().getUsername())
                .displayName(follow.getFollower().getDisplayName())
                .profileImage(follow.getFollower().getProfileImage())
                .followedAt(follow.getCreatedAt())
                .build();
    }

    // Used for "who I follow" lists -> show the followed user's profile
    public FollowUserSummaryDTO toFollowingSummary(Follow follow) {
        return FollowUserSummaryDTO.builder()
                .userId(follow.getFollowing().getId())
                .username(follow.getFollowing().getUsername())
                .displayName(follow.getFollowing().getDisplayName())
                .profileImage(follow.getFollowing().getProfileImage())
                .followedAt(follow.getCreatedAt())
                .build();
    }
}