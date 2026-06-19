package com.wordle.blog.mapper;

import org.springframework.stereotype.Component;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enitity.User;

@Component

public class UserMapper {
    public User mapCreateUserDtoRequesttoUser(CreateUserRequestDto request) {
        return User.builder()
                .displayName(request.getDisplayName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

    }

    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
