package com.dsd.rdstore.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Indicadores agregados del Inicio, calculados en la base de datos. */
public record DashboardResumenDTO(
        LocalDate fechaReferencia,
        BigDecimal ventasHoy,
        Long cantidadVentasHoy,
        Long ventasSinCobertura,
        Long unidadesSinCobertura,
        // Nulos cuando el usuario no tiene permiso para consultar inventario.
        Long productosStockBajo,
        Long lotesProximosVencer,
        Long lotesVencidos,
        Integer umbralStockBajo,
        List<DashboardVentaRecienteDTO> ultimasVentas
) {}
