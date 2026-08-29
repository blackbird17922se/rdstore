package com.dsd.rdstore.dto.categoria;

import jakarta.validation.constraints.NotNull;

public record CategoriaEstadoDTO(
    @NotNull(message = "El estado es obligatorio") Boolean activo
) {}
