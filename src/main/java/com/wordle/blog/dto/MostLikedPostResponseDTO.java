package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MostLikedPostResponseDTO {
    private Long postId;
    private String title;
    private Long likeCount;
}