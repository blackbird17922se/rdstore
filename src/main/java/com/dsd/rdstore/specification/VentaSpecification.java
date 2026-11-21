package com.dsd.rdstore.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.model.enums.EnumEstadoVenta;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/** Historial de ventas: filtros opcionales, sin modificar operaciones comerciales. */
public final class VentaSpecification {

    private VentaSpecification() {
        // Clase de utilidad: no instanciable.
    }

    public static Specification<Venta> filtrar(
            String texto,
            EnumEstadoVenta estado,
            Long idCliente,
            Long idVendedor,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal totalMin,
            BigDecimal totalMax) {

        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();

            // Busca por ID exacto (#123 o 123), cliente o usuario vendedor.
            if (texto != null && !texto.isBlank()) {
                String buscado = texto.trim();
                String patron = "%" + escaparLike(buscado.toLowerCase(Locale.ROOT)) + "%";
                List<Predicate> coincidencias = new ArrayList<>();
                coincidencias.add(cb.like(cb.lower(
                        root.join("cliente", JoinType.LEFT).get("nombresApellidos")), patron, '\\'));
                coincidencias.add(cb.like(cb.lower(
                        root.join("idVendedor", JoinType.LEFT).get("nombreUsuario")), patron, '\\'));

                String numero = buscado.startsWith("#") ? buscado.substring(1) : buscado;
                if (numero.matches("[0-9]+")) {
                    try {
                        coincidencias.add(cb.equal(root.get("id"), Long.parseLong(numero)));
                    } catch (NumberFormatException ignorado) {
                        // Un número fuera del rango Long no puede coincidir con el ID.
                    }
                }
                condiciones.add(cb.or(coincidencias.toArray(Predicate[]::new)));
            }

            if (estado != null) {
                condiciones.add(cb.equal(root.get("estado"), estado));
            }
            if (idCliente != null) {
                condiciones.add(cb.equal(root.get("cliente").get("id"), idCliente));
            }
            if (idVendedor != null) {
                condiciones.add(cb.equal(root.get("idVendedor").get("id"), idVendedor));
            }
            // La fecha de venta es LocalDateTime: incluir todo el último día.
            if (fechaDesde != null) {
                condiciones.add(cb.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("fecha"), fechaDesde.atStartOfDay()));
            }
            if (fechaHasta != null) {
                condiciones.add(cb.lessThan(
                        root.<LocalDateTime>get("fecha"), fechaHasta.plusDays(1).atStartOfDay()));
            }
            if (totalMin != null) {
                condiciones.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("total"), totalMin));
            }
            if (totalMax != null) {
                condiciones.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("total"), totalMax));
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    // Evita que % y _ ingresados por el usuario se traten como comodines SQL.
    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
