package com.dsd.rdstore.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.Cliente;

import jakarta.persistence.criteria.Predicate;

/** Filtros opcionales para la consulta paginada de clientes. */
public final class ClienteSpecification {

    private ClienteSpecification() {
        // Clase de utilidad: no instanciable.
    }

    public static Specification<Cliente> filtrar(
            String nombre,
            String documento,
            Boolean activo) {

        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();

            // La búsqueda por nombre es parcial y no distingue mayúsculas.
            if (nombre != null && !nombre.isBlank()) {
                String texto = "%" + escaparLike(nombre.trim().toLowerCase(Locale.ROOT)) + "%";
                filtros.add(cb.like(
                    cb.lower(root.get("nombresApellidos")), texto, '\\'
                ));
            }

            // El documento es opcional; se permite buscar una parte de él.
            if (documento != null && !documento.isBlank()) {
                String texto = "%" + escaparLike(documento.trim().toLowerCase(Locale.ROOT)) + "%";
                filtros.add(cb.like(
                    cb.lower(root.get("numeroDocumento")), texto, '\\'
                ));
            }

            // null significa consultar tanto clientes activos como inactivos.
            if (activo != null) {
                filtros.add(cb.equal(root.get("activo"), activo));
            }

            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }

    // Evita interpretar % y _ del texto ingresado como comodines SQL.
    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
