package com.dsd.rdstore.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dsd.rdstore.model.enums.EnumEstadoVenta;

public record VentaDetalleDTO(
    Long id,
    LocalDateTime fecha,
    String cliente,
    BigDecimal total,
    String vendedor,
    EnumEstadoVenta estado,
    LocalDateTime fechaAnulacion,
    String motivoAnulacion,
    List<DetalleVentaDTO> items
) {}
