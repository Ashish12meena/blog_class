package com.wordle.blog.mapper;

import com.wordle.blog.dto.CreateTagRequestDto;
import com.wordle.blog.dto.TagResponseDTO;
import com.wordle.blog.enitity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(CreateTagRequestDto request) {
        return Tag.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();
    }

    public TagResponseDTO toResponseDTO(Tag tag) {
        return TagResponseDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}