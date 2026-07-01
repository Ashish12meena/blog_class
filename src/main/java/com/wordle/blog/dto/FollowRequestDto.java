package com.wordle.blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowRequestDto {

    @NotNull(message = "Follower id is required")
    private Long followerId;

    @NotNull(message = "Following id is required")
    private Long followingId;
}