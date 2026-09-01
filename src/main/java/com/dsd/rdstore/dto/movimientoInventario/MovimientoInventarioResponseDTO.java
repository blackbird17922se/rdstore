package com.dsd.rdstore.dto.movimientoInventario;

import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;

public record MovimientoInventarioResponseDTO(

        Long id,

        Long idExistencia,

        Long idProducto,
        String nombreProducto,

        EnumTipoMovimientoInventario tipo,
        Long cantidad,

        LocalDateTime fechaMovimiento,

        EnumTipoOrigenInventario tipoOrigen,
        Long idOrigen,

        String observacion

) {
}