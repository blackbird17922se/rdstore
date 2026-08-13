package com.dsd.rdstore.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/** <b>¿Qué es JwtAuthFilter?</b>
Es un filtro HTTP.
Un filtro se ejecuta antes de que la petición llegue al controller.
Mirar si la petición trae un JWT, comprobarlo y, si es válido, 
registrar al usuario como autenticado dentro de Spring Security.

¿Por qué @Component?
@Component Le dice a Spring:
    Administra esta clase como un Bean.
Eso nos permitirá después inyectarla en SecurityConfig.
*/
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /** OncePerRequestFilter
     * es una clase de Spring, 
     * Once Per Request Filter = Ejecuta este filtro una vez por cada petición HTTP.
     * Por ejemplo:

            GET /usuarios
            → JwtAuthFilter se ejecuta

            GET /productos
            → JwtAuthFilter se ejecuta

            POST /ventas
            → JwtAuthFilter se ejecuta
     * De esa manera en cada consumo de un endpoint, valida q el token este valido y activo
    */


    // Nuestro filtro necesita JwtService porque no sabe por sí mismo trabajar con JWT.
    private final JwtService jwtService;

    /**
     * Este es el método importante, es el método que Spring ejecutará por cada
     * request.
     * parametros:
     * <<<< HttpServletRequest: >>>>>
     * Representa la petición que llegó. Por ejemplo:
     *      GET /api/v2/usuarios
     *      Authorization: Bearer eyJ...
     * 
     * <<<< HttpServletResponse response >>>>
     * Representa la respuesta que eventualmente saldrá del servidor.
     * 
     * <<< FilterChain filterChain >>>
     * Continúa con el resto de filtros y eventualmente llega al controller.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        /**
         * Cuando Postman envíe:
         *      Authorization: Bearer eyJhbGciOi...
         * 
         * entonces authorizationHeader contendrá:
         *      Bearer eyJhbGciOi...
         * 
         * Todavía no tenemos únicamente el token. Tenemos:
         *      Bearer + espacio + JWT
         */
        String authorizationHeader =
                request.getHeader("Authorization");

        /** indica si No mandaron el header Authorization
         * o si Mandaron Authorization, pero no comienza por "Bearer "...
         */
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            /*
             * aqui estamos diciendo: Ya terminé mi trabajo. Continúa procesando esta
             * petición.
             * Y aqui el proceso q hace es:
             * Mi filtro JWT no puede autenticarlo; que Spring Security continúe y decida si
             * esa ruta requiere autenticación.
             */
            filterChain.doFilter(request, response);
            return;
        }

        // sacar la palabra Bearer (B e a r e r espacio)
        String token = authorizationHeader.substring(7);

        // Validar el token
        if (jwtService.validarToken(token)) {

            String nombreUsuario =
                    jwtService.extraerNombreUsuario(token);

            String rol =
                    jwtService.extraerRol(token);

            /*
             * SimpleGrantedAuthority. Si:
             *      rol = ADMIN
             * construimos:
             *      ROLE_ADMIN
             * 
             * ¿Por qué agregamos ROLE_?
             * Porque Spring Security tiene una convención.
             * Cuando posteriormente escribamos:
             *      hasRole("ADMIN")
             * Spring internamente compara contra:
             *      ROLE_ADMIN
             */
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + rol);

            /** objeto q almacena
             * nombreUsuario
             * credencial (pero esta ya no es necesaria porque se valido en el login, por eso null)
             * List.of(authority): el rol con notacion spring
             */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            nombreUsuario,
                            null,
                            List.of(authority)
                    );

            /** Aquí estamos diciéndole oficialmente a Spring Security:
             * Este request pertenece a un usuario autenticado. */
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // Esto significa: Ya revisé el JWT. Continúa procesando la petición.
        filterChain.doFilter(request, response);
    }
}