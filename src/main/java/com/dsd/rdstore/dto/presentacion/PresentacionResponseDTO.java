package com.dsd.rdstore.dto.presentacion;

public record PresentacionResponseDTO(
    Long id,
    String nombre,
    Boolean activo
) {}