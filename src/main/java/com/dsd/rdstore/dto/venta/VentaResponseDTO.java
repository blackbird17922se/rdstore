package com.dsd.rdstore.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.EnumEstadoVenta;

public record VentaResponseDTO(
        Long id,
        LocalDateTime fecha,
        Long idCliente,
        String nombreCliente,
        BigDecimal subtotal,
        BigDecimal ivaTotal,
        BigDecimal total,
        Long idVendedor,
        String vendedor,
        EnumEstadoVenta estado,
        LocalDateTime fechaAnulacion,
        String motivoAnulacion
) {}
