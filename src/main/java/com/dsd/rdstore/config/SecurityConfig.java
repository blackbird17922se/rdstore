package com.dsd.rdstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import com.dsd.rdstore.security.JwtAccessDeniedHandler;
import com.dsd.rdstore.security.JwtAuthFilter;
import com.dsd.rdstore.security.JwtAuthenticationEntryPoint;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

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
                .cors(withDefaults())
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

                    // Público
                    .requestMatchers("/api/v2/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()

                    // Perfil propio: cualquier usuario autenticado
                    .requestMatchers("/api/v2/usuarios/perfil/**")
                        .authenticated()

                    // ==========================================
                    // VENTAS
                    // ==========================================

                    // Anulación: solo ADMIN
                    .requestMatchers(
                        HttpMethod.PATCH,
                        "/api/v2/ventas/*/anular"
                    )
                        .hasRole("ADMIN")

                    // Venta, historial y detalle
                    .requestMatchers("/api/v2/ventas/**")
                        .hasAnyRole("ADMIN", "VENDEDOR")

                    // ==========================================
                    // CLIENTES
                    // ==========================================

                    .requestMatchers("/api/v2/clientes/**")
                        .hasAnyRole("ADMIN", "VENDEDOR")

                    // ==========================================
                    // PRODUCTOS - CONSULTA
                    // ==========================================

                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v2/productos/**"
                    )
                        .hasAnyRole("ADMIN", "VENDEDOR")

                    // Modificación de productos: ADMIN
                    .requestMatchers("/api/v2/productos/**")
                        .hasRole("ADMIN")

                    // ==========================================
                    // ADMINISTRACIÓN
                    // ==========================================

                    .requestMatchers("/api/v2/usuarios/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/roles/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/tarifas-iva/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/marcas/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/presentaciones/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/categorias/**")
                        .hasRole("ADMIN")

                    // ==========================================
                    // INVENTARIO
                    // ==========================================

                    .requestMatchers("/api/v2/entradas-inventario/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/existencias/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/movimientos-inventario/**")
                        .hasRole("ADMIN")

                    .requestMatchers("/api/v2/ajustes-inventario/**")
                        .hasRole("ADMIN")

                    // Todo endpoint no contemplado requiere login
                    .anyRequest()
                        .authenticated()
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}