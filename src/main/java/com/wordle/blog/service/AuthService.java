package com.wordle.blog.service;

import com.wordle.blog.dto.AuthResponseDTO;
import com.wordle.blog.dto.LoginRequestDto;
import com.wordle.blog.dto.RefreshTokenRequestDto;
import com.wordle.blog.dto.RegisterRequestDto;
import com.wordle.blog.enitity.RefreshToken;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.Role;
import com.wordle.blog.exception.EmailAlreadyExistException;
import com.wordle.blog.exception.InvalidCredentialsException;
import com.wordle.blog.exception.UsernameAlreadyExist;
import com.wordle.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.wordle.blog.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder; // add this

   

    @Transactional
    public AuthResponseDTO login(LoginRequestDto request) {
        try {
            // Delegates to DaoAuthenticationProvider, which internally loads
            // the user via UserDetailsService and checks the password using
            // PasswordEncoder.matches() against the BCrypt hash. We never
            // compare passwords ourselves.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username '{}'", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User '{}' logged in successfully", user.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public AuthResponseDTO refresh(RefreshTokenRequestDto request) {
        RefreshToken existing = refreshTokenService.validateAndGet(request.getRefreshToken());
        User user = existing.getUser();

        // Rotate the refresh token: revoke the old one, issue a new one.
        // This limits how long a stolen refresh token stays useful — if
        // someone replays an old (already-used) refresh token, it'll be
        // rejected because it's marked revoked.
        refreshTokenService.revoke(existing.getToken());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), user.getRole().name());

        log.info("Refreshed tokens for user '{}'", user.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequestDto request) {
        refreshTokenService.revoke(request.getRefreshToken());
        log.info("Refresh token revoked (logout)");
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration attempted with existing username '{}'", request.getUsername());
            throw new UsernameAlreadyExist("Username already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempted with existing email '{}'", request.getEmail());
            throw new EmailAlreadyExistException("Email already registered: " + request.getEmail());
        }

        // We NEVER store request.getPassword() directly — always run it
        // through PasswordEncoder.encode() first. This is the one and only
        // place a raw password should ever touch this codebase before
        // being irreversibly hashed.
        User user = User.builder()
                .displayName(request.getDisplayName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // self-registration is always USER; nobody
                                 // signs themselves up as ADMIN through a
                                 // public endpoint — that's a deliberate
                                 // security boundary, not an oversight
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: '{}'", saved.getUsername());

        // Auto-login on register: issue tokens immediately so the
        // frontend doesn't need a separate "now go log in" round trip.
        String accessToken = jwtService.generateAccessToken(saved.getUsername(), saved.getId(), saved.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(saved);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(saved.getId())
                .username(saved.getUsername())
                .build();
    }
}