package com.dsd.rdstore.dto.producto;

import java.math.BigDecimal;

public record ProductoResponseDTO(
    Long id,
    String codigoBarras,
    String nombre,
    String descripcion,
    BigDecimal precio,

    Long idMarca,
    String nombreMarca,

    Long idCategoria,
    String nombreCategoria,

    Long idPresentacion,
    String nombrePresentacion,

    Long idTarifaIva,
    String nombreTarifaIva,
    BigDecimal porcentajeIva,

    Long stock,
    Boolean activo,
    Boolean controlaVencimiento
) {}