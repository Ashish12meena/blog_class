package com.wordle.blog.service;

import com.wordle.blog.enitity.RefreshToken;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.InvalidRefreshTokenException;
import com.wordle.blog.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Refresh tokens are plain random UUID strings, NOT JWTs. There's
     * nothing to "decode" — their only job is to be an unguessable key we
     * look up in the database. The actual user identity and expiry live in
     * the RefreshToken row itself, not encoded in the token string.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000))
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.info("Issued refresh token for user {}", user.getId());
        return saved;
    }

    public RefreshToken validateAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", token);
                    return new InvalidRefreshTokenException("Invalid refresh token");
                });

        if (refreshToken.isRevoked()) {
            log.warn("Attempt to use revoked refresh token for user {}", refreshToken.getUser().getId());
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Expired refresh token used for user {}", refreshToken.getUser().getId());
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            log.info("Revoked refresh token for user {}", rt.getUser().getId());
        });
    }
}