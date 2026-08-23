package com.dsd.rdstore.dto.entradaInventario;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record EntradaInventarioRequestDTO(

        @NotNull(message = "La fecha de entrada es obligatoria")
        @PastOrPresent(
            message = "La fecha de entrada no puede ser futura"
        )
        LocalDate fechaEntrada,

        @Size(
            max = 100,
            message = "El número de documento no puede superar los 100 caracteres"
        )
        String numeroDocumento,

        @Size(
            max = 500,
            message = "La observación no puede superar los 500 caracteres"
        )
        String observacion,

        @NotEmpty(
            message = "La entrada debe contener al menos un producto"
        )
        @Valid
        List<DetalleEntradaInventarioRequestDTO> detalles

) {
}