package com.dsd.rdstore.dto.tarifaiva;

import jakarta.validation.constraints.NotNull;

public record TarifaIvaEstadoDTO(

    @NotNull(message = "El estado es obligatorio")
    Boolean activo

) {}