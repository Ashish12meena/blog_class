package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeCheckResponseDTO {
    private Long userId;
    private Long postId;
    private boolean liked;
}