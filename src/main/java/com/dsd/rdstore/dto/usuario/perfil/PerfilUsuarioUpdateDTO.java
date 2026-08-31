package com.dsd.rdstore.dto.usuario.perfil;

import jakarta.validation.constraints.NotBlank;

public record PerfilUsuarioUpdateDTO(
    @NotBlank String nombre,
    @NotBlank String apellido
) {}