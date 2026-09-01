package com.dsd.rdstore.dto.venta;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record VentaRequestDTO(
        Long idCliente,

        @Size(max = 500)
        String observacion,

        @NotEmpty
        @Valid
        List<DetalleVentaRequestDTO> detalles
) {}
