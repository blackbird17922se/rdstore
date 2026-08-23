package com.dsd.rdstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dsd.rdstore.security.JwtAccessDeniedHandler;
import com.dsd.rdstore.security.JwtAuthFilter;
import com.dsd.rdstore.security.JwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                /*
                 * cada petición debe traer su propia identificación.
                 * Esa identificación será nuestro JWT.
                 */
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                    )
                )

                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers("/api/v2/auth/**").permitAll()

                        // Solo autenticados
                        .requestMatchers("/api/v2/perfil/**")
                            .authenticated()
                        .requestMatchers("/api/v2/marcas/**")
                            .authenticated()
                        .requestMatchers("/api/v2/productos/**")
                            .authenticated()

                        // Administración de usuarios
                        .requestMatchers("/api/v2/usuarios/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/v2/roles/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/v2/tarifas-iva/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v2/presentaciones/**")
                        .hasRole("ADMIN")
                        
                        .anyRequest().authenticated()
                )

                /*
                 *      Ejecuta mi JwtAuthFilter antes del filtro estándar
                 *      UsernamePasswordAuthenticationFilter.
                 */
                .addFilterBefore(
                    jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                )


                .build();
    }

    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}