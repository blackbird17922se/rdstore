package com.dsd.rdstore.dto.auth;

public record LoginResponseDTO(
        String token,
        String tipo,
        String nombreUsuario,
        String rol
) {}