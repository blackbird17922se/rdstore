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
                 * Spring Security permite configurar explícitamente la política de sesión; para
                 * una API basada en tokens, STATELESS indica que el contexto de seguridad no
                 * debe mantenerse mediante una sesión HTTP entre peticiones.
                 * 
                 * En palabras sencillas:
                 *  cada petición debe traer su propia identificación.
                 * Esa identificación será nuestro JWT.
                 */
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                    )
                )

                /*
                 * Spring Security permite configurar ambos manejadores mediante
                 * exceptionHandling: uno para autenticación faltante y otro para acceso
                 * denegado.
                 */
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        /*
                        .requestMatchers(HttpMethod.GET, "/api/v2/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v2/usuarios")
                        */

                        // Login público
                        .requestMatchers("/api/v2/auth/**").permitAll()

                        // Solo autenticados
                        .requestMatchers("/api/v2/perfil/**")
                            .authenticated()

                        // Administración de usuarios
                        .requestMatchers("/api/v2/usuarios/**")
                        .hasRole("ADMIN") // Solo el rol admin podra acceder aqui

                        .requestMatchers("/api/v2/roles/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v2/tarifas-iva/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v2/presentaciones/**")
                        .hasRole("ADMIN")
                        // Spring agrega automáticamente el prefijo: ROLE_
                        // en JwtAuthFilter / new SimpleGrantedAuthority("ROLE_" + rol);

                        .requestMatchers("/api/v2/marcas/**")
                            .authenticated()
                        .requestMatchers("/api/v2/productos/**")
                            .authenticated()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                /*
                 * Aquí finalmente conectamos nuestra clase JwtAuthFilter.
                 * Le estamos diciendo a Spring Security:
                 *      Ejecuta mi JwtAuthFilter antes del filtro estándar
                 *      UsernamePasswordAuthenticationFilter.
                 * y como mi clase JwtAuthFilter hereda de OncePerRequestFilter,
                 * su metodo tipo override doFilterInternal el cual sobreescribimos,
                 * sera la "puerta de entrada" al resto de procesos
                 */
                .addFilterBefore(
                    jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                )


                .build();
    }

    // Para encriptar y validar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}