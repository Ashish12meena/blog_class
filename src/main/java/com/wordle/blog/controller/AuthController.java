// package com.wordle.blog.controller;

// import com.wordle.blog.dto.AuthResponseDTO;
// import com.wordle.blog.dto.CreateUserRequestDto;
// import com.wordle.blog.dto.LoginRequestDto;
// import com.wordle.blog.dto.RefreshTokenRequestDto;
// import com.wordle.blog.dto.UserResponseDTO;
// import com.wordle.blog.service.AuthService;
// import com.wordle.blog.service.UserService;

// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @Slf4j
// @RestController
// @RequestMapping("auth")
// public class AuthController {

//     private final AuthService authService;
//     private final UserService userService;

//     public AuthController(AuthService authService,
//             UserService userService) {
//         this.authService = authService;
//         this.userService = userService;
//     }

//     @PostMapping("/login")
//     public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDto request) {
//         log.info("Login attempt for username '{}'", request.getUsername());
//         return ResponseEntity.ok(authService.login(request));
//     }

//     @PostMapping("/refresh")
//     public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
//         return ResponseEntity.ok(authService.refresh(request));
//     }

//     @PostMapping("/logout")
//     public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
//         authService.logout(request);
//         return ResponseEntity.noContent().build();
//     }

//     @PostMapping("/register")
//     public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody CreateUserRequestDto request) {
//         UserResponseDTO response = userService.createUser(request);
//         return new ResponseEntity<>(response, HttpStatus.CREATED);

//     }
// }