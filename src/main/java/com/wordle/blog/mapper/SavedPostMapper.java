package com.wordle.blog.mapper;

import com.wordle.blog.dto.SavedPostResponseDTO;
import com.wordle.blog.dto.SavedPostSummaryDTO;
import com.wordle.blog.enitity.SavedPost;
import org.springframework.stereotype.Component;

@Component
public class SavedPostMapper {

    public SavedPostResponseDTO toResponseDTO(SavedPost savedPost) {
        return SavedPostResponseDTO.builder()
                .id(savedPost.getId())
                .userId(savedPost.getUser().getId())
                .postId(savedPost.getPost().getId())
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    public SavedPostSummaryDTO toSummaryDTO(SavedPost savedPost) {
        return SavedPostSummaryDTO.builder()
                .postId(savedPost.getPost().getId())
                .title(savedPost.getPost().getTitle())
                .authorId(savedPost.getPost().getCreatedBy().getId())
                .authorUsername(savedPost.getPost().getCreatedBy().getUsername())
                .savedAt(savedPost.getCreatedAt())
                .build();
    }
}