package com.dsd.rdstore.dto.venta;

import java.math.BigDecimal;

public record DetalleVentaDTO(
    Long id,
    Long idVenta,
    String producto,
    Long cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal
) {}