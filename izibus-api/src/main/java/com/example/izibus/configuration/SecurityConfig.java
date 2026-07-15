package com.example.izibus.configuration;

import com.example.izibus.filter.JwtFilter;
import com.example.izibus.services.CustomUserDetailsService;
import com.example.izibus.configuration.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())  //  Autoriser CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/register/admin",
                                "/api/v1/login/admin",
                                "/api/v1/register/compagnie",
                                "/api/v1/login/compagnie",
                                "/api/v1/register/client",
                                "/api/v1/verify/client",
                                "/api/v1/trajets/**",
                                "/api/v1/compagnies",
                                "/api/v1/resend-otp/client",
                                "/api/v1/clients/telephone/**",
                                "/api/v1/clients/**",
                                "/api/v1/compagnies/**",
                                "/api/v1/reservations/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/compagnie/**","/api/v1/compagnies/{id}").hasRole("COMPAGNIE")
                        .requestMatchers("/api/v1/reservations/**").hasAnyRole("COMPAGNIE", "CLIENT")
                        .requestMatchers("/api/v1/paiements/**").hasAnyRole("COMPAGNIE", "CLIENT")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.accessDeniedHandler((request, response, accessDeniedException) -> {
                    logger.warn("🚨 ACCÈS REFUSÉ ! URI : {}", request.getRequestURI());
                    response.sendError(403, "Accès refusé !");
                }))
                .addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
