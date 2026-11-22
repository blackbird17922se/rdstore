package com.dsd.rdstore.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.dsd.rdstore.model.RefreshSession;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.RefreshSessionRepository;
import com.dsd.rdstore.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final RefreshSessionRepository sessions;
    private final UsuarioRepository usuarios;

    @Value("${jwt.refresh-expiration:72000000}")
    private long refreshExpirationMillis;

    public record SessionIssued(String secret, Instant expiresAt) {}
    public record SessionRotated(String secret, Instant expiresAt, Usuario usuario) {}

    /** La expiración absoluta se define al iniciar sesión; renovar NO la extiende. */
    @Transactional
    public SessionIssued crear(String nombreUsuario) {
        Usuario usuario = usuarios.findByNombreUsuarioAndActivoTrue(nombreUsuario)
                .orElseThrow(() -> noAutorizado());
        Instant expiry = Instant.now().plusMillis(refreshExpirationMillis);
        String raw = crearSecreto();
        RefreshSession session = new RefreshSession();
        session.setIdUsuario(usuario.getId());
        session.setTokenHash(hash(raw));
        session.setExpiresAt(expiry);
        sessions.save(session);
        return new SessionIssued(raw, expiry);
    }

    /** Rotación atómica: el secreto previo deja de funcionar tras esta transacción. */
    @Transactional
    public SessionRotated rotar(String raw) {
        if (raw == null || raw.isBlank()) throw noAutorizado();
        RefreshSession session = sessions.findByHashForUpdate(hash(raw))
                .orElseThrow(() -> noAutorizado());
        if (session.isRevoked() || !session.getExpiresAt().isAfter(Instant.now())) {
            throw noAutorizado();
        }
        Usuario usuario = usuarios.findById(session.getIdUsuario())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> noAutorizado());
        String nuevo = crearSecreto();
        session.setTokenHash(hash(nuevo));
        return new SessionRotated(nuevo, session.getExpiresAt(), usuario);
    }

    /** No requiere JWT válido: permite cerrar una sesión cuyo access token venció. */
    @Transactional
    public void revocar(String raw) {
        if (raw == null || raw.isBlank()) return;
        sessions.findByHashForUpdate(hash(raw)).ifPresent(s -> s.setRevoked(true));
    }

    private static String crearSecreto() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private static ResponseStatusException noAutorizado() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión vencida o inválida");
    }
}
