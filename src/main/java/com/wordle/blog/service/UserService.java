package com.wordle.blog.service;

import org.springframework.stereotype.Service;

import com.wordle.blog.dto.CreateUserDtoRequest;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.UserMapper;
import com.wordle.blog.repository.UserRepository;

@Service
public class UserService {

    UserRepository usrRepo;
    UserMapper userMapper;

    UserService(UserRepository userRepository) {
        this.usrRepo = userRepository;
    }

    public void findById(Long id) {
        User user = usrRepo.findById(id).orElseThrow(() -> new UserNotFoundException("user not found at id:" + id));
    }

    public User createUser(CreateUserDtoRequest request) {
        User user = userMapper.mapCreateUserDtoRequesttoUser(request);
        return usrRepo.save(user);
    }

}
