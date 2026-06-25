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
public class LikePostRequestDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Post id is required")
    private Long postId;
}