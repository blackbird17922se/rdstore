package com.dsd.rdstore.dto.cliente;

import com.dsd.rdstore.model.enums.EnumTipoDocumento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(

    @Size(max = 20)
    EnumTipoDocumento tipoDocumento,

    @Size(max = 30)
    String numeroDocumento ,

    @NotBlank(message = "El nombre del deudor no puede ser vacio")
    @Size(message = "En nombre del deudor no puede contener mas de 100 caracteres", max = 100)
    String nombresApellidos,

    @Size(max = 30)
    String telefono,

    @Size(max = 150)
    String correo,

    @Size(max = 250)
    String direccion,

    @Size(max = 500)
    String observacion
) {

}
