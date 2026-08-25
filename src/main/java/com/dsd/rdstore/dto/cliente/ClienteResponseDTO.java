package com.dsd.rdstore.dto.cliente;

import java.time.LocalDate;

import com.dsd.rdstore.model.enums.EnumTipoDocumento;

public record ClienteResponseDTO(

    Long id,
    EnumTipoDocumento tipoDocumento,
    String numeroDocumento ,
    String nombresApellidos,
    String telefono,
    String correo,
    String direccion,
    String observacion,
    LocalDate fechaRegistro,
    Boolean activo
) {}
