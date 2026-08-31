package com.dsd.rdstore.dto.tarifaiva;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.EnumTipoIva;

public record TarifaIvaResponseDTO(

    Long id,
    String nombre,
    EnumTipoIva tipo,
    BigDecimal porcentaje,
    Boolean activo

) {}