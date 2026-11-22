package com.dsd.rdstore.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.enums.EnumEstadoVencimiento;

import jakarta.persistence.criteria.Predicate;

/**
 * Filtros de solo lectura para alertas por lote.
 * No reemplaza la Specification administrativa de Existencias.
 */
public final class AlertaVencimientoSpecification {

    private AlertaVencimientoSpecification() {
        // Utilidad sin estado.
    }

    public static Specification<ExistenciaProducto> filtrar(
            String texto,
            Long idProducto,
            EnumEstadoVencimiento estado,
            Integer dias,
            LocalDate vencimientoDesde,
            LocalDate vencimientoHasta,
            LocalDate hoy) {

        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();

            // Solo existencias físicas con unidades y fecha de vencimiento.
            filtros.add(cb.greaterThan(root.<Long>get("cantidad"), 0L));
            filtros.add(cb.isNotNull(root.get("fechaVencimiento")));

            if (texto != null && !texto.isBlank()) {
                String patron = "%" + escaparLike(texto.trim().toLowerCase(Locale.ROOT)) + "%";
                filtros.add(cb.or(
                        cb.like(cb.lower(root.get("producto").get("nombre")), patron, '\\'),
                        cb.like(cb.lower(root.get("producto").get("codigoBarras")), patron, '\\'),
                        cb.like(cb.lower(root.get("numeroLote")), patron, '\\')));
            }

            if (idProducto != null) {
                filtros.add(cb.equal(root.get("producto").get("id"), idProducto));
            }

            // Vencidos no se excluyen; el horizonte limita solo la fecha máxima.
            if (dias != null) {
                filtros.add(cb.lessThanOrEqualTo(root.<LocalDate>get("fechaVencimiento"),
                        hoy.plusDays(dias)));
            }

            if (vencimientoDesde != null) {
                filtros.add(cb.greaterThanOrEqualTo(
                        root.<LocalDate>get("fechaVencimiento"), vencimientoDesde));
            }
            if (vencimientoHasta != null) {
                filtros.add(cb.lessThanOrEqualTo(
                        root.<LocalDate>get("fechaVencimiento"), vencimientoHasta));
            }

            if (estado != null) {
                switch (estado) {
                    case VENCIDO -> filtros.add(cb.lessThan(root.<LocalDate>get("fechaVencimiento"), hoy));
                    case CRITICO -> filtros.add(cb.between(root.<LocalDate>get("fechaVencimiento"),
                            hoy, hoy.plusDays(7)));
                    case PROXIMO -> filtros.add(cb.between(root.<LocalDate>get("fechaVencimiento"),
                            hoy.plusDays(8), hoy.plusDays(30)));
                    case SEGUIMIENTO -> filtros.add(cb.between(root.<LocalDate>get("fechaVencimiento"),
                            hoy.plusDays(31), hoy.plusDays(60)));
                    case SIN_ALERTA -> filtros.add(cb.greaterThan(root.<LocalDate>get("fechaVencimiento"),
                            hoy.plusDays(60)));
                }
            }
            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }

    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
