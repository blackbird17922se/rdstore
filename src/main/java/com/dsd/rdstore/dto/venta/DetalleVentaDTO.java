package com.dsd.rdstore.dto.venta;

public record DetalleVentaDTO(
    Integer id,
    Integer idVenta,
    String producto,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
) {}