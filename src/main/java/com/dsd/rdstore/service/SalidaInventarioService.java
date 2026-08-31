package com.dsd.rdstore.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

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

    @Transactional
    public void descontarPorVenta(Producto producto, Long cantidadSolicitada, Long idVenta) {

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

            existencia.setCantidad(disponible - cantidadADescontar);

            existenciaProductoRepository.save(existencia);

            registrarMovimientoVenta(
                    existencia,
                    cantidadADescontar,
                    idVenta);

            pendiente -= cantidadADescontar;
        }

        if (pendiente > 0) {

            log.warn(
                    "Venta {} realizada con faltante de inventario. "
                            + "Producto: {}, cantidad sin stock: {}",
                    idVenta,
                    producto.getId(),
                    pendiente);
        }
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

}
