package com.dsd.rdstore.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    String nombre
) {}
