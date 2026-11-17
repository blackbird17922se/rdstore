package com.dsd.rdstore.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import com.dsd.rdstore.model.Presentacion;
import jakarta.persistence.criteria.Predicate;

/** Filtros exclusivos de Presentaciones. */
public final class PresentacionSpecification {
    private PresentacionSpecification() {}

    public static Specification<Presentacion> filtrar(String nombre, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            if (nombre != null && !nombre.isBlank()) {
                String patron = "%" + nombre.trim().toLowerCase(Locale.ROOT) + "%";
                condiciones.add(cb.like(cb.lower(root.get("nombre")), patron));
            }
            if (activo != null) {
                condiciones.add(cb.equal(root.get("activo"), activo));
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }
}
