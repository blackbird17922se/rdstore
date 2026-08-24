package com.dsd.rdstore.dto.ajusteInventario;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.TipoAjusteInventario;

public record AjusteInventarioResponseDTO(

        Long id,

        Long idExistencia,

        Long idProducto,
        String nombreProducto,

        String numeroLote,
        LocalDate fechaVencimiento,

        TipoAjusteInventario tipo,
        Long cantidad,

        String motivo,
        String observacion,

        LocalDateTime fechaAjuste,

        Long idUsuario,
        String nombreUsuario

) {
}