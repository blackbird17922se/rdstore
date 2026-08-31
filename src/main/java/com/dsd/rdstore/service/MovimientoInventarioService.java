package com.dsd.rdstore.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.movimientoInventario.MovimientoInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.MovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.MovimientoInventarioRepository;
import com.dsd.rdstore.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de registrar y consultar los movimientos
 * que modifican las existencias del inventario.
 *
 * Cada movimiento representa una entrada o salida de una existencia
 * específica y conserva la referencia de la operación de negocio
 * que originó dicho cambio.
 *
 * Los movimientos forman parte del historial del inventario y no
 * deben modificarse ni eliminarse después de ser registrados.
 */
@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ExistenciaProductoRepository existenciaProductoRepository;
    private final ProductoRepository productoRepository;


    public MovimientoInventario registrarMovimiento(
            ExistenciaProducto existencia,
            EnumTipoMovimientoInventario tipo,
            Long cantidad,
            EnumTipoOrigenInventario tipoOrigen,
            Long idOrigen,
            String observacion) {

        validarCantidadMovimiento(tipo, cantidad);

        MovimientoInventario movimiento =
                new MovimientoInventario();

                System.out.println("Zona JVM: " + java.time.ZoneId.systemDefault());
System.out.println("Hora Java: " + LocalDateTime.now());

        movimiento.setExistencia(existencia);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoOrigen(tipoOrigen);
        movimiento.setIdOrigen(idOrigen);
        movimiento.setObservacion(
                normalizarTexto(observacion)
        );

        return movimientoInventarioRepository.save(movimiento);
    }


    public List<MovimientoInventarioResponseDTO>
            listarPorExistencia(Long idExistencia) {

        validarExistencia(idExistencia);

        return movimientoInventarioRepository
                .findByExistenciaIdOrderByFechaMovimientoDesc(
                        idExistencia)
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<MovimientoInventarioResponseDTO>
            listarPorProducto(Long idProducto) {

        validarProducto(idProducto);

        return movimientoInventarioRepository
                .findByExistenciaProductoIdOrderByFechaMovimientoDesc(
                        idProducto)
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<MovimientoInventarioResponseDTO>
            listarPorOrigen(
                    EnumTipoOrigenInventario tipoOrigen,
                    Long idOrigen) {

        return movimientoInventarioRepository
                .findByTipoOrigenAndIdOrigenOrderByIdAsc(
                        tipoOrigen,
                        idOrigen)
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    private void validarCantidadMovimiento(
            EnumTipoMovimientoInventario tipo,
            Long cantidad) {

        if (cantidad == null || cantidad == 0) {
            throw new BusinessRuleException(
                    "La cantidad del movimiento debe ser diferente de cero"
            );
        }

        if ((tipo == EnumTipoMovimientoInventario.ENTRADA
                || tipo == EnumTipoMovimientoInventario.AJUSTE_ENTRADA)
                && cantidad < 0) {

            throw new BusinessRuleException(
                    "Los movimientos de entrada deben tener una cantidad positiva"
            );
        }

        if ((tipo == EnumTipoMovimientoInventario.VENTA
                || tipo == EnumTipoMovimientoInventario.AJUSTE_SALIDA)
                && cantidad > 0) {

            throw new BusinessRuleException(
                    "Los movimientos de salida deben tener una cantidad negativa"
            );
        }
    }


    private void validarExistencia(Long id) {

        if (!existenciaProductoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Existencia de producto",
                    id
            );
        }
    }


    private void validarProducto(Long id) {

        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Producto",
                    id
            );
        }
    }


    private String normalizarTexto(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }


    private MovimientoInventarioResponseDTO mapResponse(
            MovimientoInventario movimiento) {

        return new MovimientoInventarioResponseDTO(
                movimiento.getId(),

                movimiento.getExistencia().getId(),

                movimiento.getExistencia().getProducto().getId(),
                movimiento.getExistencia().getProducto().getNombre(),

                movimiento.getTipo(),
                movimiento.getCantidad(),

                movimiento.getFechaMovimiento(),

                movimiento.getTipoOrigen(),
                movimiento.getIdOrigen(),

                movimiento.getObservacion()
        );
    }
}