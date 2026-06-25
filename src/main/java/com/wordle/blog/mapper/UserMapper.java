package com.wordle.blog.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.Role;

@Component
public class UserMapper {
    public User mapCreateUserDtoRequesttoUser(CreateUserRequestDto request) {
        return User.builder()
                .displayName(request.getDisplayName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.valueOf(request.getRole()))
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

    public List<UserResponseDTO> mapListOfEntityToListOfUserResponseDto(List<User> users) {
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();

        for (User user : users) {
            // UserResponseDTO userResponseDTO = new UserResponseDTO();
            // userResponseDTO.setId(user.getId());
            // userResponseDTO.setBio(user.getBio());
            // userResponseDTO.setDisplayName(user.getDisplayName());
            // userResponseDTO.setEmail(user.getEmail());
            // userResponseDTO.setProfileImage(user.getProfileImage());
            // userResponseDTO.setRole(user.getRole());
            // userResponseDTO.setCreatedAt(user.getCreatedAt());
            // userResponseDTO.setActive(user.isActive());

          UserResponseDTO userResponseDTO =   UserResponseDTO.builder()
                    .bio(user.getBio())
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .profileImage(user.getProfileImage())
                    .role(user.getRole())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .isActive(user.isActive())
                    .build();

            userResponseDTOs.add(userResponseDTO);
        }

        return userResponseDTOs;
    }

}
