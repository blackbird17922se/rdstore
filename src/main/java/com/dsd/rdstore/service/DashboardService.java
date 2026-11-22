package com.dsd.rdstore.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.dsd.rdstore.dto.dashboard.DashboardResumenDTO;
import com.dsd.rdstore.dto.dashboard.DashboardVentaRecienteDTO;
import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.model.enums.EnumEstadoVenta;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * Indicadores de lectura para la pantalla Inicio.
 * Las cifras se agregan en MySQL: NO se descargan todas las ventas ni existencias.
 * No modifica ventas, existencias ni reglas FIFO/FEFO.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Bogota");
    private static final int MIN_UMBRAL = 0;
    private static final int MAX_UMBRAL = 1000;
    private static final int MAX_VENTAS_RECIENTES = 5;

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public DashboardResumenDTO obtenerResumen(int umbralStockBajo, Authentication authentication) {
        if (umbralStockBajo < MIN_UMBRAL || umbralStockBajo > MAX_UMBRAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El umbral de stock bajo debe estar entre 0 y 1000");
        }

        boolean admin = tieneRol(authentication, "ROLE_ADMIN");
        boolean vendedor = tieneRol(authentication, "ROLE_VENDEDOR");
        if (!admin && !vendedor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tiene permisos para consultar el dashboard");
        }

        LocalDate hoy = LocalDate.now(ZONA_NEGOCIO);
        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hastaExclusivo = hoy.plusDays(1).atStartOfDay();

        // El total de hoy considera únicamente ventas CONFIRMADAS; excluye anuladas.
        Object[] ventas = (Object[]) entityManager.createQuery("""
                SELECT COUNT(v.id), SUM(v.total)
                FROM Venta v
                WHERE v.estado = :estado
                  AND v.fecha >= :desde
                  AND v.fecha < :hasta
                """)
                .setParameter("estado", EnumEstadoVenta.CONFIRMADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hastaExclusivo)
                .getSingleResult();

        Long cantidadVentasHoy = ((Number) ventas[0]).longValue();
        BigDecimal ventasHoy = ventas[1] == null ? BigDecimal.ZERO : (BigDecimal) ventas[1];

        // Sin cobertura = diferencia de inventario, NO equivale a entrega pendiente.
        // Ventas anteriores a esta funcionalidad tienen cantidadSinStock = null.
        Object[] faltantes = (Object[]) entityManager.createQuery("""
                SELECT COUNT(DISTINCT d.venta.id), SUM(d.cantidadSinStock)
                FROM DetalleVenta d
                WHERE d.venta.estado = :estado
                  AND d.cantidadSinStock > 0
                """)
                .setParameter("estado", EnumEstadoVenta.CONFIRMADA)
                .getSingleResult();

        Long ventasSinCobertura = ((Number) faltantes[0]).longValue();
        Long unidadesSinCobertura = faltantes[1] == null
                ? 0L : ((Number) faltantes[1]).longValue();

        // Solo cinco ventas recientes; nunca se usa findAll() para el dashboard.
        List<Venta> ventasRecientes = entityManager.createQuery("""
                SELECT v FROM Venta v ORDER BY v.fecha DESC, v.id DESC
                """, Venta.class)
                .setMaxResults(MAX_VENTAS_RECIENTES)
                .getResultList();

        List<DashboardVentaRecienteDTO> ultimasVentas = ventasRecientes.stream()
                .map(v -> new DashboardVentaRecienteDTO(
                        v.getId(), v.getFecha(),
                        v.getCliente() == null ? "Consumidor final"
                                : v.getCliente().getNombresApellidos(),
                        v.getTotal(), v.getEstado()))
                .toList();

        // Los endpoints de inventario son de ADMIN: no filtrar estas métricas al VENDEDOR.
        Long productosStockBajo = null;
        Long lotesProximosVencer = null;
        Long lotesVencidos = null;

        if (admin) {
            // Stock vendible: ignorar lotes vencidos de productos con control de vencimiento.
            // Contar productos activos sin existencias como stock cero.
            productosStockBajo = ((Number) entityManager.createQuery("""
                    SELECT COUNT(p.id)
                    FROM Producto p
                    WHERE p.activo = true
                      AND (SELECT COALESCE(SUM(e.cantidad), 0)
                           FROM ExistenciaProducto e
                           WHERE e.producto = p
                             AND e.cantidad > 0
                             AND (p.controlaVencimiento = false
                                  OR (e.fechaVencimiento IS NOT NULL
                                      AND e.fechaVencimiento >= :hoy))) <= :umbral
                    """)
                    .setParameter("hoy", hoy)
                    .setParameter("umbral", (long) umbralStockBajo)
                    .getSingleResult()).longValue();

            // Contadores de LOTES, no de unidades ni de productos distintos.
            lotesProximosVencer = ((Number) entityManager.createQuery("""
                    SELECT COUNT(e.id) FROM ExistenciaProducto e
                    WHERE e.cantidad > 0
                      AND e.fechaVencimiento >= :hoy
                      AND e.fechaVencimiento <= :limite
                    """)
                    .setParameter("hoy", hoy)
                    .setParameter("limite", hoy.plusDays(30))
                    .getSingleResult()).longValue();

            lotesVencidos = ((Number) entityManager.createQuery("""
                    SELECT COUNT(e.id) FROM ExistenciaProducto e
                    WHERE e.cantidad > 0
                      AND e.fechaVencimiento < :hoy
                    """)
                    .setParameter("hoy", hoy)
                    .getSingleResult()).longValue();
        }

        return new DashboardResumenDTO(
                hoy, ventasHoy, cantidadVentasHoy,
                ventasSinCobertura, unidadesSinCobertura,
                productosStockBajo, lotesProximosVencer, lotesVencidos,
                umbralStockBajo, ultimasVentas);
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .anyMatch(a -> rol.equals(a.getAuthority()));
    }
}
