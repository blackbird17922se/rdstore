package com.dsd.rdstore.dto.usuario;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String nombreUsuario,
        Boolean activo,
        Long idRol,
        String nombreRol
) {}