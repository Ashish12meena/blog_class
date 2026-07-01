package com.wordle.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// All fields optional -> only non-null fields are applied (partial update)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequestDto {

    @Size(min = 1, max = 100, message = "Display name must be 1-100 characters")
    private String displayName;

    @Size(max = 500, message = "Bio must be under 500 characters")
    private String bio;

    private String profileImage;
}