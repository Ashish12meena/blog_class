package com.wordle.blog.mapper;

import com.wordle.blog.dto.CreatePostRequestDto;
import com.wordle.blog.dto.PostResponseDTO;
import com.wordle.blog.enitity.Post;
import com.wordle.blog.enitity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(CreatePostRequestDto request, User author) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdBy(author)
                .build();
    }

    public PostResponseDTO toResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .createdById(post.getCreatedBy().getId())
                .createdByUsername(post.getCreatedBy().getUsername())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}