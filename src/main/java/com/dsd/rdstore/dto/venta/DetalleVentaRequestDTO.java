package com.dsd.rdstore.dto.venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaRequestDTO(

        @NotNull
        Long idProducto,

        @NotNull
        @Positive
        Long cantidad

) {}