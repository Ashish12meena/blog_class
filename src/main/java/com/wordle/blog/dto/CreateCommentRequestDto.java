package com.wordle.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Post id is required")
    private Long postId;

    // null for a top-level comment, set for a reply
    private Long parentCommentId;

    @NotBlank(message = "Content is required")
    private String content;
}