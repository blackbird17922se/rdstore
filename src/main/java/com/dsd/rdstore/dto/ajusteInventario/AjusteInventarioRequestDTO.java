package com.dsd.rdstore.dto.ajusteInventario;

import com.dsd.rdstore.model.enums.TipoAjusteInventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AjusteInventarioRequestDTO(

        @NotNull(message = "La existencia es obligatoria")
        Long idExistencia,

        @NotNull(message = "El tipo de ajuste es obligatorio")
        TipoAjusteInventario tipo,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Long cantidad,

        @NotBlank(message = "El motivo del ajuste es obligatorio")
        @Size(
            max = 150,
            message = "El motivo no puede superar los 150 caracteres"
        )
        String motivo,

        @Size(
            max = 500,
            message = "La observación no puede superar los 500 caracteres"
        )
        String observacion

) {
}