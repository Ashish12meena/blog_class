package com.wordle.blog.mapper;

import com.wordle.blog.dto.CategoryResponseDTO;
import com.wordle.blog.dto.CreateCategoryRequestDto;
import com.wordle.blog.enitity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequestDto request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public CategoryResponseDTO toResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}