package com.example.izibus.configuration;

import com.example.izibus.filter.JwtFilter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.izibus.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtFilter jwtFilter;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtUtils jwtUtils,
            JwtFilter jwtFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http,
                                                PasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    /**
     * Actuator
     */
    @Bean
    @Order(1)
    SecurityFilterChain actuator(HttpSecurity http) throws Exception {

        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * API
     */
    @Bean
    @Order(2)
    SecurityFilterChain api(HttpSecurity http) throws Exception {

        http

                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(

                                "/api/v1/register/admin",
                                "/api/v1/login/admin",

                                "/api/v1/register/client",
                                "/api/v1/login/client",

                                "/api/v1/register/compagnie",
                                "/api/v1/login/compagnie",

                                "/api/v1/verify/client",

                                "/api/v1/trajets/**",

                                "/api/v1/compagnies/**",

                                "/api/v1/reservations/**",

                                "/api/v1/resend-otp/client",

                                "/api/v1/clients/**"

                        ).permitAll()

                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v1/compagnie/**")
                        .hasRole("COMPAGNIE")

                        .requestMatchers("/api/v1/paiements/**")
                        .hasAnyRole("CLIENT", "COMPAGNIE")

                        .anyRequest()
                        .authenticated()

                )

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}