package com.wordle.blog.mapper;

import com.wordle.blog.dto.LikeResponseDTO;
import com.wordle.blog.enitity.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {
    public LikeResponseDTO toResponseDTO(Like like) {
        return LikeResponseDTO.builder()
                .id(like.getId())
                .userId(like.getLikedBy().getId())
                .postId(like.getPost().getId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}