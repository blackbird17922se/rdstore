package com.dsd.rdstore.dto.usuario;

public record UsuarioUpdateDTO(
        String nombre,
        String apellido,
        Long idRol
) {}
