package com.wordle.blog.controller;

import com.wordle.blog.dto.AuthResponseDTO;
import com.wordle.blog.dto.LoginRequestDto;
import com.wordle.blog.dto.RefreshTokenRequestDto;
import com.wordle.blog.dto.RegisterRequestDto;
import com.wordle.blog.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login attempt for username '{}'", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDto request) {
      AuthResponseDTO authResponseDTO =  authService.register(request);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.CREATED);

    }
}