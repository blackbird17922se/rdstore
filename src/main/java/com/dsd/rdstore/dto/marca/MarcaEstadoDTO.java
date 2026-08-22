package com.dsd.rdstore.dto.marca;

import jakarta.validation.constraints.NotNull;

public record MarcaEstadoDTO(

        @NotNull(message = "El estado es obligatorio") Boolean activo) {

}
