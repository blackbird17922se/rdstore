package com.dsd.rdstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                        .requestMatchers(HttpMethod.GET, "/api/v2/roles").permitAll()
                        .requestMatchers("/api/v2/auth/**").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    // Para encriptar y validar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}