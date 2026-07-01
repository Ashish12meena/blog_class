package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long postId;
    private Long parentCommentId;
    private String content;
    private long replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}