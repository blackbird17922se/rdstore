package com.dsd.rdstore.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import com.dsd.rdstore.model.AjusteInventario;
import com.dsd.rdstore.model.enums.EnumTipoAjusteInventario;
import jakarta.persistence.criteria.Predicate;

/** Búsqueda de ajustes históricos: solamente construye filtros de lectura. */
public final class AjusteInventarioSpecification {
    private AjusteInventarioSpecification() { }

    public static Specification<AjusteInventario> filtrar(
            String texto, EnumTipoAjusteInventario tipo, Long idProducto,
            Long idExistencia, LocalDate fechaDesde, LocalDate fechaHasta) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (texto != null && !texto.isBlank()) {
                String patron = "%" + escaparLike(texto.trim().toLowerCase(Locale.ROOT)) + "%";
                filtros.add(cb.or(
                        cb.like(cb.lower(root.get("existencia").get("producto").get("nombre")), patron, '\\'),
                        cb.like(cb.lower(root.get("existencia").get("numeroLote")), patron, '\\'),
                        cb.like(cb.lower(root.get("motivo")), patron, '\\'),
                        cb.like(cb.lower(root.get("observacion")), patron, '\\')));
            }
            if (tipo != null) filtros.add(cb.equal(root.get("tipo"), tipo));
            if (idProducto != null) filtros.add(cb.equal(root.get("existencia").get("producto").get("id"), idProducto));
            if (idExistencia != null) filtros.add(cb.equal(root.get("existencia").get("id"), idExistencia));
            // fechaAjuste es LocalDateTime; el día final se incluye por completo.
            if (fechaDesde != null) filtros.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fechaAjuste"), fechaDesde.atStartOfDay()));
            if (fechaHasta != null) filtros.add(cb.lessThan(root.<LocalDateTime>get("fechaAjuste"), fechaHasta.plusDays(1).atStartOfDay()));
            return cb.and(filtros.toArray(Predicate[]::new));
        };
    }

    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
