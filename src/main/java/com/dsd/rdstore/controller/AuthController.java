package com.dsd.rdstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.auth.LoginRequestDTO;
import com.dsd.rdstore.dto.auth.LoginResponseDTO;
import com.dsd.rdstore.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import com.dsd.rdstore.service.RefreshSessionService;
import com.dsd.rdstore.security.JwtService;
import com.dsd.rdstore.model.Usuario;
import java.time.Duration;
import java.util.Set;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshSessionService refreshSessions;
    private final JwtService jwtService;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-same-site:Lax}")
    private String sameSite;

    // Coincide con los orígenes permitidos por CORS, no con el host del API.
    private static final Set<String> ORIGENES_PERMITIDOS = Set.of(
            "http://localhost:4200",
            "https://icy-bay-07fd19b0f.5.azurestaticapps.net");

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {

        validarOrigen(request);
        LoginResponseDTO response = authService.login(dto);
        RefreshSessionService.SessionIssued session = refreshSessions.crear(dto.nombreUsuario());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                cookie(session.secret(), session.expiresAt()).toString()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(
            @CookieValue(name = "dstore_refresh", required = false) String raw,
            HttpServletRequest request) {
        validarOrigen(request);
        RefreshSessionService.SessionRotated nueva = refreshSessions.rotar(raw);
        Usuario usuario = nueva.usuario();
        LoginResponseDTO response = new LoginResponseDTO(
                jwtService.generarToken(usuario), "Bearer",
                usuario.getNombreUsuario(), usuario.getRol().getNombre());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                cookie(nueva.secret(), nueva.expiresAt()).toString()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "dstore_refresh", required = false) String raw,
            HttpServletRequest request) {
        validarOrigen(request);
        refreshSessions.revocar(raw);
        ResponseCookie clear = ResponseCookie.from("dstore_refresh", "")
                .httpOnly(true).secure(cookieSecure).sameSite(sameSite)
                .path("/api/v2/auth").maxAge(Duration.ZERO).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clear.toString()).build();
    }

    private ResponseCookie cookie(String secret, java.time.Instant expiry) {
        long segundos = Math.max(0, Duration.between(java.time.Instant.now(), expiry).getSeconds());
        return ResponseCookie.from("dstore_refresh", secret)
                .httpOnly(true).secure(cookieSecure).sameSite(sameSite)
                .path("/api/v2/auth").maxAge(Duration.ofSeconds(segundos)).build();
    }

    /** Defensa CSRF para los endpoints que aceptan cookies de sesión. */
    private void validarOrigen(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null || !ORIGENES_PERMITIDOS.contains(origin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Origen no autorizado");
        }
    }

}
