package com.wordle.blog.dto;

import java.time.LocalDateTime;

import com.wordle.blog.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDtoRequest {
    private String displayName;

    private String username;

    private String email;

    private String password;

    private Role role;

}
