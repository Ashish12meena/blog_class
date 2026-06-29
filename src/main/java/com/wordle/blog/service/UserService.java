package com.wordle.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.Role;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.exception.UsernameAlreadyExist;
import com.wordle.blog.mapper.UserMapper;
import com.wordle.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository usrRepo;
    private final UserMapper userMapper;
    // private final PasswordEncoder passwordEncoder;

    public void findById(Long id) {
        User user = usrRepo.findById(id).orElseThrow(() -> new UserNotFoundException("user not found at id:" + id));
        UserResponseDTO ru = new UserResponseDTO();

    }

    public UserResponseDTO createUser(CreateUserRequestDto request) {
        if (isEmailAlreadyExist(request.getEmail()) || isUsernameExist(request.getUsername())) {
            throw new UsernameAlreadyExist("user with this username" + request.getUsername() + " already exist");
        }
        // String hashedPassword = passwordEncoder.encode(request.getPassword());
        // request.setPassword(hashedPassword);
        User user = userMapper.mapCreateUserDtoRequesttoUser(request);
        return userMapper.toResponseDTO(usrRepo.save(user));
    }

    public boolean isEmailAlreadyExist(String email) {
        return usrRepo.existsByEmail(email);
    }

    public boolean isUsernameExist(String username) {
        return usrRepo.existsByUsername(username);
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

    // public List<UserResponseDTO> getAllUsers() {
    // // List<User> users = usrRepo.findAll();

    // // return userMapper.mapListOfEntityToListOfUserResponseDto(users);

    // // return usrRepo.findAllByProjection();

    // }

}
