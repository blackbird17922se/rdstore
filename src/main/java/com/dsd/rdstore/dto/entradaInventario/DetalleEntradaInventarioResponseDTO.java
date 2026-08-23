package com.dsd.rdstore.dto.entradaInventario;

import java.time.LocalDate;

public record DetalleEntradaInventarioResponseDTO(

        Long id,

        Long idProducto,
        String nombreProducto,

        Long cantidad,

        String numeroLote,
        LocalDate fechaVencimiento

) {
}