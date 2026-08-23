package com.dsd.rdstore.dto.entradaInventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EntradaInventarioResponseDTO(

        Long id,

        LocalDate fechaEntrada,
        LocalDateTime fechaRegistro,

        String numeroDocumento,
        String observacion,

        List<DetalleEntradaInventarioResponseDTO> detalles

) {
}