package com.dsd.rdstore.dto.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequestDTO(

        @Size(
            max = 50,
            message = "El código de barras no puede superar los 50 caracteres"
        )
        String codigoBarras,

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(
            max = 150,
            message = "El nombre no puede superar los 150 caracteres"
        )
        String nombre,

        @Size(
            max = 500,
            message = "La descripción no puede superar los 500 caracteres"
        )
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "El precio no puede ser negativo"
        )
        BigDecimal precio,

        @NotNull(message = "La marca es obligatoria")
        Long idMarca,

        @NotNull(message = "La categoría es obligatoria")
        Long idCategoria,

        @NotNull(message = "La presentación es obligatoria")
        Long idPresentacion,

        @NotNull(message = "La tarifa de IVA es obligatoria")
        Long idTarifaIva,

        @NotNull(message = "El control de vencimiento es obligatorio")
        Boolean controlaVencimiento

) {}