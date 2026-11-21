package com.dsd.rdstore.dto.entradaInventario;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Cabecera de una entrada para listados: no carga los detalles de cada registro. */
public record EntradaInventarioListadoDTO(
        Long id,
        LocalDate fechaEntrada,
        LocalDateTime fechaRegistro,
        String numeroDocumento,
        String observacion) {
}
