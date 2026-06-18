package com.wordle.blog.mapper;

import org.springframework.stereotype.Component;

import com.wordle.blog.dto.CreateUserDtoRequest;
import com.wordle.blog.enitity.User;

@Component
public class UserMapper {

    public User mapCreateUserDtoRequesttoUser(CreateUserDtoRequest request) {
        return User.builder()
                .displayName(request.getDisplayName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();


    }

}
