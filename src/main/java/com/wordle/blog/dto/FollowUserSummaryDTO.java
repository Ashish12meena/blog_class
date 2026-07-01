package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Lightweight user representation shown inside a follower/following list
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserSummaryDTO {
    private Long userId;
    private String username;
    private String displayName;
    private String profileImage;
    private LocalDateTime followedAt;
}