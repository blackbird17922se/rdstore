package com.dsd.rdstore.dto.venta;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.EnumTipoIva;

public record DetalleVentaDTO(
    Long id,
    Long idVenta,
    String producto,
    Long cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal,
    /** Importe total de la línea, con IVA incluido. */
    BigDecimal total,
    BigDecimal valorIva,
    BigDecimal porcentajeIva,
    EnumTipoIva tipoIva,
    Long idProducto,
    /** Null en registros históricos: no se puede asumir que sea cero. */
    Long cantidadDescontada,
    /** Cantidad vendida sin respaldo en stock, no necesariamente pendiente de entrega. */
    Long cantidadSinStock
) {}