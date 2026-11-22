package com.dsd.rdstore.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.EntradaInventario;

import jakarta.persistence.criteria.Predicate;

/** Filtros opcionales para consultar cabeceras de entradas sin alterar el inventario. */
public final class EntradaInventarioSpecification {

    private EntradaInventarioSpecification() {
        // Clase de utilidad: no instanciable.
    }

    public static Specification<EntradaInventario> filtrar(
            String texto,
            String numeroDocumento,
            LocalDate fechaEntradaDesde,
            LocalDate fechaEntradaHasta,
            LocalDate fechaRegistroDesde,
            LocalDate fechaRegistroHasta) {

        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();

            // Búsqueda general: identificador exacto, número de documento u observación.
            if (texto != null && !texto.isBlank()) {
                String valor = texto.trim();
                String patron = "%" + escaparLike(valor.toLowerCase(Locale.ROOT)) + "%";
                List<Predicate> coincidencias = new ArrayList<>();
                coincidencias.add(cb.like(cb.lower(root.get("numeroDocumento")), patron, '\\'));
                coincidencias.add(cb.like(cb.lower(root.get("observacion")), patron, '\\'));
                try {
                    coincidencias.add(cb.equal(root.get("id"), Long.parseLong(valor.replaceFirst("^#", ""))));
                } catch (NumberFormatException ignored) {
                    // Un texto no numérico simplemente no busca por identificador.
                }
                filtros.add(cb.or(coincidencias.toArray(Predicate[]::new)));
            }

            if (numeroDocumento != null && !numeroDocumento.isBlank()) {
                String patronDocumento = "%" + escaparLike(numeroDocumento.trim().toLowerCase(Locale.ROOT)) + "%";
                filtros.add(cb.like(cb.lower(root.get("numeroDocumento")), patronDocumento, '\\'));
            }

            // fechaEntrada es LocalDate: ambos extremos son inclusivos.
            if (fechaEntradaDesde != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.<LocalDate>get("fechaEntrada"), fechaEntradaDesde));
            }
            if (fechaEntradaHasta != null) {
                filtros.add(cb.lessThanOrEqualTo(root.<LocalDate>get("fechaEntrada"), fechaEntradaHasta));
            }

            // fechaRegistro es LocalDateTime: el límite superior incluye todo el día.
            if (fechaRegistroDesde != null) {
                filtros.add(cb.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("fechaRegistro"), fechaRegistroDesde.atStartOfDay()));
            }
            if (fechaRegistroHasta != null) {
                filtros.add(cb.lessThan(
                        root.<LocalDateTime>get("fechaRegistro"), fechaRegistroHasta.plusDays(1).atStartOfDay()));
            }

            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }

    // Se escapan comodines introducidos por el usuario, como en Existencias.
    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
