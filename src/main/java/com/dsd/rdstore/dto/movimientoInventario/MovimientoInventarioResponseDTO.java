package com.dsd.rdstore.dto.movimientoInventario;

import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.TipoMovimientoInventario;
import com.dsd.rdstore.model.enums.TipoOrigenInventario;

public record MovimientoInventarioResponseDTO(

        Long id,

        Long idExistencia,

        Long idProducto,
        String nombreProducto,

        TipoMovimientoInventario tipo,
        Long cantidad,

        LocalDateTime fechaMovimiento,

        TipoOrigenInventario tipoOrigen,
        Long idOrigen,

        String observacion

) {
}