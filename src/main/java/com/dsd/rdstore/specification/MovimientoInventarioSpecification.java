package com.dsd.rdstore.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.MovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;

import jakarta.persistence.criteria.Predicate;

/** Filtros opcionales de consulta; no interviene en el registro ni en FIFO/FEFO. */
public final class MovimientoInventarioSpecification {

    private MovimientoInventarioSpecification() {
        // Clase de utilidad: no instanciable.
    }

    public static Specification<MovimientoInventario> filtrar(
            String texto,
            Long idProducto,
            Long idExistencia,
            EnumTipoMovimientoInventario tipo,
            EnumTipoOrigenInventario tipoOrigen,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();

            // Búsqueda general por nombre, código de barras, lote o ID exacto del movimiento.
            if (texto != null && !texto.isBlank()) {
                String valor = texto.trim();
                String patron = "%" + escaparLike(valor.toLowerCase(Locale.ROOT)) + "%";
                List<Predicate> coincidencias = new ArrayList<>();
                coincidencias.add(cb.like(
                        cb.lower(root.get("existencia").get("producto").get("nombre")), patron, '\\'));
                coincidencias.add(cb.like(
                        cb.lower(root.get("existencia").get("producto").get("codigoBarras")), patron, '\\'));
                coincidencias.add(cb.like(
                        cb.lower(root.get("existencia").get("numeroLote")), patron, '\\'));

                try {
                    coincidencias.add(cb.equal(root.get("id"), Long.parseLong(valor.replaceFirst("^#", ""))));
                } catch (NumberFormatException ignored) {
                    // Si no es un número, se busca únicamente por texto.
                }

                filtros.add(cb.or(coincidencias.toArray(Predicate[]::new)));
            }

            if (idProducto != null) {
                filtros.add(cb.equal(root.get("existencia").get("producto").get("id"), idProducto));
            }
            if (idExistencia != null) {
                filtros.add(cb.equal(root.get("existencia").get("id"), idExistencia));
            }
            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipo"), tipo));
            }
            if (tipoOrigen != null) {
                filtros.add(cb.equal(root.get("tipoOrigen"), tipoOrigen));
            }

            // fechaMovimiento es LocalDateTime: incluimos el día completo de fechaHasta.
            if (fechaDesde != null) {
                filtros.add(cb.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("fechaMovimiento"), fechaDesde.atStartOfDay()));
            }
            if (fechaHasta != null) {
                filtros.add(cb.lessThan(
                        root.<LocalDateTime>get("fechaMovimiento"), fechaHasta.plusDays(1).atStartOfDay()));
            }

            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }

    // % y _ se buscan literalmente, no como comodines SQL.
    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
