package com.wordle.blog.dto;

import com.wordle.blog.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String displayName;
    private String username;
    private String email;
    private String bio;
    private Role role;
    private String profileImage;
    private boolean isActive;
    private LocalDateTime createdAt;
}
