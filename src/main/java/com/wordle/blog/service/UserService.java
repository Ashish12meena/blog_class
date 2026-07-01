package com.wordle.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wordle.blog.dto.CreateUserRequestDto;
import com.wordle.blog.dto.UpdateUserRequestDto;
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

    public UserResponseDTO findById(Long id) {
        User user = usrRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found at id:" + id));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO findByUsername(String username) {
        User user = usrRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user not found with username:" + username));
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDto request) {
        User user = usrRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found at id:" + id));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        return userMapper.toResponseDTO(usrRepo.save(user));
    }

    public UserResponseDTO createUser(CreateUserRequestDto request) {
        if (isEmailAlreadyExist(request.getEmail()) || isUsernameExist(request.getUsername())) {
            throw new UsernameAlreadyExist("user with this username " + request.getUsername() + " already exist");
        }
        // NOTE: registration normally goes through AuthService/AuthController, which
        // encodes the password correctly. This method is currently NOT wired to any
        // controller. If you ever expose it, the password MUST be BCrypt-encoded
        // before save - right now it is stored as-is. Prefer /auth/register instead.
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
}