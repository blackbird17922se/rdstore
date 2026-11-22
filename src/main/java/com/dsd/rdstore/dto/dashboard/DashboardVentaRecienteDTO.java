package com.dsd.rdstore.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.EnumEstadoVenta;

/** Venta reciente mostrada en el Inicio, sin descargar todos sus detalles. */
public record DashboardVentaRecienteDTO(
        Long id,
        LocalDateTime fecha,
        String cliente,
        BigDecimal total,
        EnumEstadoVenta estado
) {}
