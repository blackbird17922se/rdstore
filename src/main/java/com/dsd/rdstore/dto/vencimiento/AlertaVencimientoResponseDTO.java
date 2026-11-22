package com.dsd.rdstore.dto.vencimiento;

import java.time.LocalDate;

import com.dsd.rdstore.model.enums.EnumEstadoVencimiento;

/** Datos de una alerta por existencia/lote; no altera el DTO de Existencias. */
public record AlertaVencimientoResponseDTO(
        Long idExistencia,
        Long idProducto,
        String nombreProducto,
        Long cantidad,
        String numeroLote,
        LocalDate fechaVencimiento,
        long diasRestantes,
        EnumEstadoVencimiento estado) {
}
