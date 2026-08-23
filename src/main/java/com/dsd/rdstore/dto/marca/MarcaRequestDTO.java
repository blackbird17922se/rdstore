package com.dsd.rdstore.dto.marca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarcaRequestDTO(
    @NotBlank(message = "El nombre de la marca es obligatoria")
    @Size(
        max = 100,
        message = "El nombre de la marca no puede superar los 100 caracteres"
    )
    String nombre
) {}
