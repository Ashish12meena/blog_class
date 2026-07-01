package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowCountResponseDTO {
    private Long userId;
    private long followerCount;
    private long followingCount;
}