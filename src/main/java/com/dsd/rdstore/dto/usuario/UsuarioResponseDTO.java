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

/* DTO no debería transportar entidades completas.
DTO debería transportar datos simples. */