package com.dsd.rdstore.dto.entradaInventario;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DetalleEntradaInventarioRequestDTO(

        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Long cantidad,

        @Size(
            max = 100,
            message = "El número de lote no puede superar los 100 caracteres"
        )
        String numeroLote,

        @FutureOrPresent(
            message = "La fecha de vencimiento no puede ser anterior a la fecha actual"
        )
        LocalDate fechaVencimiento

) {
}