package com.dsd.rdstore.dto.vencimiento;

/** Contadores de lotes con stock, no suma de unidades. */
public record ResumenVencimientosDTO(
        long vencidos,
        long criticos,
        long proximos,
        long seguimiento,
        long sinAlerta) {
}
