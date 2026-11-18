package com.dsd.rdstore.specification.common;

import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.common.util.PaginationUtils;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionResponseDTO;
import com.dsd.rdstore.specification.PresentacionSpecification;

public final class CommonSpecifications {

    private static final Set<String> CAMPOS_ORDEN = Set.of("id", "nombre");


    private CommonSpecifications() {}

    public static <T> Specification<T> nombreContiene(String nombre) {

        return (root, query, cb) -> {

            if (nombre == null || nombre.isBlank()) {
                return cb.conjunction();
            }

            String valor = "%" +
                    nombre.trim().toLowerCase(Locale.ROOT) + "%";

            return cb.like(
                    cb.lower(root.get("nombre")),
                    valor
            );
        };
    }

    public static <T> Specification<T> tieneEstado(Boolean activo) {

        return (root, query, cb) -> {

            if (activo == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("activo"), activo);
        };
    }

}