package com.wordle.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequestDto {

    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
}