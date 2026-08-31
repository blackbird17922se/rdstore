package com.dsd.rdstore.dto.venta;

import java.time.LocalDateTime;
import java.util.List;

public record VentaDetalleDTO(
    Integer id,
    LocalDateTime fecha,
    String cliente,
    Double total,
    String vendedor,
    String estado,
    LocalDateTime fechaAnulacion,
    String motivoAnulacion,
    List<DetalleVentaDTO> items
) {}
