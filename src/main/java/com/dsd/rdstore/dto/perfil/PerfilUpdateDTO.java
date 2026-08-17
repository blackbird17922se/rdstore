package com.dsd.rdstore.dto.perfil;

import jakarta.validation.constraints.NotBlank;

public record PerfilUpdateDTO(

    @NotBlank
    String nombre,

    @NotBlank
    String apellido

) {}