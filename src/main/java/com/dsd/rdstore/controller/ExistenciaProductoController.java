package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.existencia.ExistenciaProductoResponseDTO;
import com.dsd.rdstore.service.ExistenciaProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/existencias")
public class ExistenciaProductoController {

    private final ExistenciaProductoService existenciaProductoService;


    @GetMapping("/{id}")
    public ResponseEntity<ExistenciaProductoResponseDTO>
            obtenerExistencia(@PathVariable Long id) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .obtenerExistenciaPorId(id)
        );
    }


    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarExistenciasPorProducto(idProducto)
        );
    }


    @GetMapping("/producto/{idProducto}/disponibles")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarDisponiblesPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarExistenciasDisponiblesPorProducto(
                                idProducto
                        )
        );
    }


    @GetMapping("/proximas-vencer")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarProximosAVencer(
                    @RequestParam(defaultValue = "30")
                    Integer dias) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarProximosAVencer(dias)
        );
    }
}