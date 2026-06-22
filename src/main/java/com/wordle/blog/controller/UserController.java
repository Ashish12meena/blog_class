package com.wordle.blog.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enums.Role;
import com.wordle.blog.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/user")
public class UserController {
    UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody CreateUserRequestDto request) {
        UserResponseDTO response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {

        List<UserResponseDTO> userResponseDTOs = userService.getAllUsers();
        return userResponseDTOs;

    }
}
