package com.dsd.rdstore.dto.tarifaiva;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.TipoIva;

public record TarifaIvaResponseDTO(

    Long id,
    String nombre,
    TipoIva tipo,
    BigDecimal porcentaje,
    Boolean activo

) {}