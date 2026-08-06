package com.dsd.rdstore.dto.usuario;

public record UsuarioPasswordDTO(
        String contrasenaActual,
        String nuevaContrasena
) {}
