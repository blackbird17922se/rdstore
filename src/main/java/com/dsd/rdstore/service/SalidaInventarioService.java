package com.dsd.rdstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.MovimientoInventario;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.MovimientoInventarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalidaInventarioService {

    private final ExistenciaProductoRepository existenciaProductoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    /**
     * Cantidades realmente afectadas en el inventario físico registrado.
     * Las unidades sin stock no generan movimientos de salida ficticios.
     */
    public record ResultadoDescuentoInventario(long cantidadDescontada, long cantidadSinStock) {}

    @Transactional
    public ResultadoDescuentoInventario descontarPorVenta(
            Producto producto, Long cantidadSolicitada, Long idVenta) {

        // Validación defensiva: el servicio también puede ser invocado fuera del Controller.
        if (producto == null || producto.getId() == null) {
            throw new BusinessRuleException("El producto de la venta es obligatorio");
        }
        if (cantidadSolicitada == null || cantidadSolicitada <= 0) {
            throw new BusinessRuleException("La cantidad a descontar debe ser mayor a cero");
        }
        if (idVenta == null) {
            throw new BusinessRuleException("La venta debe estar registrada antes de descontar inventario");
        }

        List<ExistenciaProducto> existencias;

        if (Boolean.TRUE.equals(producto.getControlaVencimiento())) {

            existencias = existenciaProductoRepository
                .buscarDisponiblesFEFO(producto.getId());

        } else {

            existencias = existenciaProductoRepository
                .buscarDisponiblesFIFO(producto.getId());
        }

        long pendiente = cantidadSolicitada;

        for (ExistenciaProducto existencia : existencias) {

            if (pendiente <= 0) {
                break;
            }

            long disponible = existencia.getCantidad();

            long cantidadADescontar = Math.min(disponible, pendiente);

            // Un lote agotado no genera un movimiento de salida de cero unidades.
            if (cantidadADescontar <= 0) {
                continue;
            }

            existencia.setCantidad(disponible - cantidadADescontar);

            existenciaProductoRepository.save(existencia);

            registrarMovimientoVenta(
                    existencia,
                    cantidadADescontar,
                    idVenta);

            pendiente -= cantidadADescontar;
        }

        // El faltante se registra en el detalle de venta, no como stock negativo.
        // Una venta comercial puede haber ocurrido aunque DStore no tenga
        // existencias suficientes; esto NO supone automáticamente entrega pendiente.
        if (pendiente > 0) {
            log.warn(
                    "Venta {} registrada con unidades sin cobertura de inventario. "
                            + "Producto: {}, unidades sin stock: {}",
                    idVenta,
                    producto.getId(),
                    pendiente);
        }

        long descontada = cantidadSolicitada - pendiente;
        return new ResultadoDescuentoInventario(descontada, pendiente);
    }


    private void registrarMovimientoVenta(
            ExistenciaProducto existencia,
            long cantidad,
            Long idVenta) {

        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setExistencia(existencia);
        movimiento.setTipo(EnumTipoMovimientoInventario.VENTA);
        movimiento.setCantidad(-cantidad);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoOrigen(EnumTipoOrigenInventario.VENTA);
        movimiento.setIdOrigen(idVenta);
        movimiento.setObservacion("Salida por venta");
        movimientoInventarioRepository.save(movimiento);
    }

    @Transactional
    public void revertirVenta(Long idVenta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository
                        .findByTipoAndTipoOrigenAndIdOrigen(
                            EnumTipoMovimientoInventario.VENTA,
                            EnumTipoOrigenInventario.VENTA,
                            idVenta);

        // Orden estable de bloqueos para reducir el riesgo de interbloqueos.
        // Solo se restituyen los movimientos de salida realmente registrados.
        List<MovimientoInventario> movimientosOrdenados = new ArrayList<>(movimientos);
        movimientosOrdenados.sort(Comparator
                .comparing((MovimientoInventario m) -> m.getExistencia().getId())
                .thenComparing(MovimientoInventario::getId));

        for (MovimientoInventario movimiento : movimientosOrdenados) {

            // Bloquear la misma fila que utilizan Ajustes, FIFO y FEFO.
            // Evita perder actualizaciones si una anulación coincide con otra operación.
            Long idExistencia = movimiento.getExistencia().getId();
            ExistenciaProducto existencia = existenciaProductoRepository
                    .obtenerPorIdConBloqueo(idExistencia)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Existencia de producto", idExistencia));

            // Una salida de venta válida debe tener cantidad estrictamente negativa.
            if (movimiento.getCantidad() == null || movimiento.getCantidad() >= 0
                    || movimiento.getCantidad() == Long.MIN_VALUE) {
                throw new BusinessRuleException("Movimiento de venta inválido para anulación");
            }
            Long cantidadADevolver = -movimiento.getCantidad();

            // Evita overflow en caso de datos extremos o inconsistentes.
            existencia.setCantidad(Math.addExact(
                    existencia.getCantidad(), cantidadADevolver));

            existenciaProductoRepository.save(
                    existencia
            );

            // Movimiento compensatorio inmutable: conserva la salida VENTA original
            // y documenta que las unidades efectivamente descontadas fueron devueltas.
            // No se compensa cantidadSinStock, pues nunca se descontó físicamente.
            MovimientoInventario compensacion = new MovimientoInventario();
            compensacion.setExistencia(existencia);
            compensacion.setTipo(EnumTipoMovimientoInventario.ANULACION_VENTA);
            compensacion.setCantidad(cantidadADevolver);
            compensacion.setFechaMovimiento(LocalDateTime.now());
            compensacion.setTipoOrigen(EnumTipoOrigenInventario.VENTA);
            compensacion.setIdOrigen(idVenta);
            compensacion.setObservacion(
                    "Anulación de venta #" + idVenta + " / revierte movimiento #" + movimiento.getId());
            movimientoInventarioRepository.save(compensacion);

        }
    }

}
