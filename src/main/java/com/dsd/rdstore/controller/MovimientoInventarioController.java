package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.movimientoInventario.MovimientoInventarioResponseDTO;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
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