package com.dsd.rdstore.specification.common;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

public final class CommonSpecifications {

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