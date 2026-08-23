package com.dsd.rdstore.dto.producto;

import jakarta.validation.constraints.NotNull;

public record ProductoEstadoDTO(

        @NotNull(message = "El estado es obligatorio")
        Boolean activo

) {}