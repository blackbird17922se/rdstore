package com.dsd.rdstore.dto.usuario.perfil;

public record PerfilUsuarioResponseDTO(
    Long id,
    String nombre,
    String apellido,
    String nombreUsuario,
    String nombreRol
) {}