package com.dsd.rdstore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.movimientoInventario.MovimientoInventarioResponseDTO;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.service.MovimientoInventarioService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado de consultar el historial de movimientos
 * realizados sobre las existencias del inventario.
 *
 * Los movimientos son registros históricos generados por operaciones
 * de negocio como entradas, ventas y ajustes, por lo que no se exponen
 * operaciones para crearlos, modificarlos o eliminarlos manualmente.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    /** Historial general de solo lectura; filtros antes de paginar los resultados. */
    @GetMapping("/paginados")
    public ResponseEntity<PageResponseDTO<MovimientoInventarioResponseDTO>> listarPaginados(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idExistencia,
            @RequestParam(required = false) EnumTipoMovimientoInventario tipo,
            @RequestParam(required = false) EnumTipoOrigenInventario tipoOrigen,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaMovimiento") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion) {

        // Impedir consultas masivas sin período incluso cuando se llama al API directamente.
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede superar la fecha final");
        }

        return ResponseEntity.ok(movimientoInventarioService.listarMovimientosPaginados(
                texto, idProducto, idExistencia, tipo, tipoOrigen,
                fechaDesde, fechaHasta, page, size, ordenarPor, direccion));
    }


    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioResponseDTO>>
            listarPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                movimientoInventarioService
                        .listarPorProducto(idProducto)
        );
    }

    @GetMapping("/existencia/{idExistencia}")
    public ResponseEntity<List<MovimientoInventarioResponseDTO>>
            listarPorExistencia(
                    @PathVariable Long idExistencia) {

        return ResponseEntity.ok(
                movimientoInventarioService
                        .listarPorExistencia(idExistencia)
        );
    }

    @GetMapping("/origen")
    public ResponseEntity<List<MovimientoInventarioResponseDTO>>
            listarPorOrigen(
                    @RequestParam EnumTipoOrigenInventario tipo,
                    @RequestParam Long idOrigen) {

        return ResponseEntity.ok(
                movimientoInventarioService
                        .listarPorOrigen(tipo, idOrigen)
        );
    }

}