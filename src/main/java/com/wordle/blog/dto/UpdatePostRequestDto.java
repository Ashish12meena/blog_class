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
public class UpdatePostRequestDto {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String content;
}