package com.dsd.rdstore.exception;

public record ErrorResponseDTO(
        int status,
        String error,
        String mensaje
) {}