package com.wordle.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.Role;
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

    public User createUser(CreateUserRequestDto request) {
        User user = userMapper.mapCreateUserDtoRequesttoUser(request);
        return usrRepo.save(user);
    }


    public Page<UserResponseDTO> getUsers(Boolean isActive, Role role, Pageable pageable) {
        Page<User> userPage;

        if (isActive != null && role != null) {
            userPage = usrRepo.findByIsActiveAndRole(isActive, role, pageable);
        } else if (isActive != null) {
            userPage = usrRepo.findByIsActive(isActive, pageable);
        } else if (role != null) {
            userPage = usrRepo.findByRole(role, pageable);
        } else {
            userPage = usrRepo.findAll(pageable);
        }

        return userPage.map(userMapper::toResponseDTO);
    }

}
