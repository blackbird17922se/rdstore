package com.dsd.rdstore.dto.presentacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PresentacionRequestDTO(

    @NotBlank(message = "El nombre de la presentación es obligatorio")
    @Size(
        max = 100,
        message = "El nombre de la presentación no puede superar los 100 caracteres"
    )
    String nombre

) {}