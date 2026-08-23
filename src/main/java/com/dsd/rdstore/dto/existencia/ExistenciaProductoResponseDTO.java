package com.dsd.rdstore.dto.existencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExistenciaProductoResponseDTO(

        Long id,

        Long idProducto,
        String nombreProducto,

        Long cantidad,

        String numeroLote,
        LocalDate fechaVencimiento,
        LocalDateTime fechaIngreso

) {
}