package com.dsd.rdstore.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.Producto;

import jakarta.persistence.criteria.Predicate;

/** Filtros exclusivos del módulo Producto; paginación se gestiona en common. */
public final class ProductoSpecification {

    private ProductoSpecification() {
    }

    public static Specification<Producto> filtrar(
            String texto,
            Boolean activo,
            Long idCategoria,
            Long idMarca,
            Long idPresentacion) {

        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();

            // Un único cuadro de búsqueda para nombre y código de barras.
            if (texto != null && !texto.isBlank()) {
                String patron = "%" + texto.trim().toLowerCase(Locale.ROOT) + "%";
                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("codigoBarras")), patron)));
            }

            if (activo != null) {
                condiciones.add(cb.equal(root.get("activo"), activo));
            }
            if (idCategoria != null) {
                condiciones.add(cb.equal(root.get("categoria").get("id"), idCategoria));
            }
            if (idMarca != null) {
                condiciones.add(cb.equal(root.get("marca").get("id"), idMarca));
            }
            if (idPresentacion != null) {
                condiciones.add(cb.equal(root.get("presentacion").get("id"), idPresentacion));
            }

            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }
}
