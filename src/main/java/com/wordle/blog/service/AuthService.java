// package com.wordle.blog.service;

// import com.wordle.blog.dto.AuthResponseDTO;
// import com.wordle.blog.dto.LoginRequestDto;
// import com.wordle.blog.dto.RefreshTokenRequestDto;
// import com.wordle.blog.enitity.RefreshToken;
// import com.wordle.blog.enitity.User;
// import com.wordle.blog.exception.InvalidCredentialsException;
// import com.wordle.blog.repository.UserRepository;
// import com.wordle.blog.security.JwtService;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Slf4j
// @Service
// public class AuthService {

//     private final AuthenticationManager authenticationManager;
//     private final UserRepository userRepository;
//     private final JwtService jwtService;
//     private final RefreshTokenService refreshTokenService;

//     public AuthService(AuthenticationManager authenticationManager,
//                         UserRepository userRepository,
//                         JwtService jwtService,
//                         RefreshTokenService refreshTokenService) {
//         this.authenticationManager = authenticationManager;
//         this.userRepository = userRepository;
//         this.jwtService = jwtService;
//         this.refreshTokenService = refreshTokenService;
//     }

//     @Transactional
//     public AuthResponseDTO login(LoginRequestDto request) {
//         try {
//             // Delegates to DaoAuthenticationProvider, which internally loads
//             // the user via UserDetailsService and checks the password using
//             // PasswordEncoder.matches() against the BCrypt hash. We never
//             // compare passwords ourselves.
//             authenticationManager.authenticate(
//                     new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//             );
//         } catch (BadCredentialsException e) {
//             log.warn("Failed login attempt for username '{}'", request.getUsername());
//             throw new InvalidCredentialsException("Invalid username or password");
//         }

//         User user = userRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

//         String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), user.getRole().name());
//         RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

//         log.info("User '{}' logged in successfully", user.getUsername());

//         return AuthResponseDTO.builder()
//                 .accessToken(accessToken)
//                 .refreshToken(refreshToken.getToken())
//                 .tokenType("Bearer")
//                 .userId(user.getId())
//                 .username(user.getUsername())
//                 .build();
//     }

//     @Transactional
//     public AuthResponseDTO refresh(RefreshTokenRequestDto request) {
//         RefreshToken existing = refreshTokenService.validateAndGet(request.getRefreshToken());
//         User user = existing.getUser();

//         // Rotate the refresh token: revoke the old one, issue a new one.
//         // This limits how long a stolen refresh token stays useful — if
//         // someone replays an old (already-used) refresh token, it'll be
//         // rejected because it's marked revoked.
//         refreshTokenService.revoke(existing.getToken());
//         RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

//         String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), user.getRole().name());

//         log.info("Refreshed tokens for user '{}'", user.getUsername());

//         return AuthResponseDTO.builder()
//                 .accessToken(newAccessToken)
//                 .refreshToken(newRefreshToken.getToken())
//                 .tokenType("Bearer")
//                 .userId(user.getId())
//                 .username(user.getUsername())
//                 .build();
//     }

//     @Transactional
//     public void logout(RefreshTokenRequestDto request) {
//         refreshTokenService.revoke(request.getRefreshToken());
//         log.info("Refresh token revoked (logout)");
//     }
// }