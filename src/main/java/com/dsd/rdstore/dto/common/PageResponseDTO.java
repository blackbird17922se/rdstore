package com.dsd.rdstore.dto.common;

import java.util.List;

import org.springframework.data.domain.Page;

/** Respuesta paginada reutilizable para los distintos módulos de RDStore. */
public record PageResponseDTO<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size) {

    public static <T> PageResponseDTO<T> from(Page<T> resultado) {
        return new PageResponseDTO<>(
                resultado.getContent(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize());
    }
}
