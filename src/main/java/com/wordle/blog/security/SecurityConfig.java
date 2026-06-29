// package com.wordle.blog.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;
//     private final UserDetailsService userDetailsService;

//     public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
//                            UserDetailsService userDetailsService) {
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//         this.userDetailsService = userDetailsService;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailsService);
//         provider.setPasswordEncoder(passwordEncoder());
//         return provider;
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                 // Tier 1: completely public, no token needed at all.
//                 // Login/refresh obviously can't require a token — that's
//                 // the whole point of these endpoints existing.
//                 .requestMatchers("/auth/**").permitAll()
//                 .requestMatchers("/media/**").permitAll() // serving uploaded files publicly

//                 // Tier 2: admin-only. Spring Security checks the matcher
//                 // rules TOP-TO-BOTTOM and stops at the first match — so this
//                 // rule MUST come before the generic "/api/**" rule below,
//                 // otherwise the broader rule would match first and admin
//                 // paths would incorrectly just need "any authenticated user."
//                 .requestMatchers("/api/admin/**").hasRole("ADMIN")

//                 // Tier 3: any other endpoint just needs a valid logged-in
//                 // user, regardless of role (USER or ADMIN both pass).
//                 .requestMatchers("/api/**").authenticated()

//                 // Anything not matched above (shouldn't really happen in
//                 // this app, but a safe default) also requires auth.
//                 .anyRequest().authenticated()
//             )
//             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     // @Bean
//     // public CorsConfigurationSource corsConfigurationSource() {
//     //     CorsConfiguration configuration = new CorsConfiguration();
//     //     configuration.setAllowedOrigins(List.of("http://localhost:3000"));
//     //     configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//     //     configuration.setAllowedHeaders(List.of("*"));
//     //     configuration.setAllowCredentials(true);

//     //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//     //     source.registerCorsConfiguration("/**", configuration);
//     //     return source;
//     // }
// }