package com.dsd.rdstore.dto.auth;

public record LoginResponseDTO(
        String token,
        String tipo,
        String nombreUsuario,
        String rol
) {}
/*
    {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "nombreUsuario": "yes",
    "rol": "ADMIN"
    }
 */