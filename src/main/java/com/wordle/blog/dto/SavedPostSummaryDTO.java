package com.wordle.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Used when listing a user's saved posts -> shows post preview + when it was saved
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedPostSummaryDTO {
    private Long postId;
    private String title;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime savedAt;
}