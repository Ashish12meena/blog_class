package com.wordle.blog.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.blog.dto.CreateUserDtoRequest;
import com.wordle.blog.service.UserService;

@RestController
@RequestMapping("api/user")
public class UserController {
    UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }


    @PostMapping
    public void createUser(@RequestBody CreateUserDtoRequest request) {
        userService.createUser(request);
    }
}
