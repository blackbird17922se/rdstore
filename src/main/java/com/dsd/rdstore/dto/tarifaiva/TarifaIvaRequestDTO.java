package com.dsd.rdstore.dto.tarifaiva;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.EnumTipoIva;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarifaIvaRequestDTO(

    @NotBlank(message = "El nombre de la tarifa IVA es obligatorio")
    @Size(
        max = 80,
        message = "El nombre no puede superar los 80 caracteres"
    )
    String nombre,

    @NotNull(message = "El tipo de IVA es obligatorio")
    EnumTipoIva tipo,

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(
        value = "0.00",
        message = "El porcentaje no puede ser negativo"
    )
    @DecimalMax(
        value = "100.00",
        message = "El porcentaje no puede ser mayor a 100"
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "El porcentaje debe tener máximo dos decimales"
    )
    BigDecimal porcentaje

) {}