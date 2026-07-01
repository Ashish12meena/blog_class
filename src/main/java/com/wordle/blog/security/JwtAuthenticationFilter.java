package com.wordle.blog.security;

import com.wordle.blog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs ONCE per incoming HTTP request, before it reaches any controller.
 * Job: look at the Authorization header, and if it contains a valid JWT,
 * tell Spring Security "this request is authenticated as this user" by
 * populating the SecurityContext. If there's no token, or it's invalid, we
 * simply do nothing and let the request continue unauthenticated — it's
 * SecurityConfig's job (not this filter's) to decide whether an
 * unauthenticated request is allowed to reach a given endpoint.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No "Bearer " prefix means there's no token to even attempt
        // parsing — pass the request through untouched.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer "

        try {
            String username = jwtService.extractUsername(token);

            // Only attempt authentication if nothing has already
            // authenticated this request (avoids redundant work if this
            // filter somehow runs twice in one request).
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                userRepository.findById(jwtService.extractUserId(token)).ifPresent(user -> {
                    if (jwtService.isTokenValid(token, username)) {

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Authenticated request for user {}", username);
                    }
                });
            }
        } catch (Exception e) {
            // Deliberately swallow and log rather than throw — an invalid/
            // expired token should result in the request being treated as
            // UNauthenticated (and rejected later by SecurityConfig if the
            // endpoint requires auth), not a 500 error.
            log.warn("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}