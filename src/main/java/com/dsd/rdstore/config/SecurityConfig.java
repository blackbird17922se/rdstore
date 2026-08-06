package com.dsd.rdstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v2/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/usuarios").permitAll()
                        .requestMatchers("/api/v2/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/roles").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}