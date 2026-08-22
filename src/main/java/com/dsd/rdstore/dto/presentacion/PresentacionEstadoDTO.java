package com.dsd.rdstore.dto.presentacion;

import jakarta.validation.constraints.NotNull;

public record PresentacionEstadoDTO(

    @NotNull(message = "El estado es obligatorio")
    Boolean activo

) {}