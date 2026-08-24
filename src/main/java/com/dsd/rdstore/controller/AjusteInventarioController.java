package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioRequestDTO;
import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioResponseDTO;
import com.dsd.rdstore.service.AjusteInventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado de registrar y consultar los ajustes manuales
 * realizados sobre existencias específicas del inventario.
 *
 * El usuario responsable del ajuste se obtiene automáticamente
 * a partir de la sesión autenticada.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ajustes-inventario")
public class AjusteInventarioController {

    private final AjusteInventarioService ajusteInventarioService;

    @PostMapping
    public ResponseEntity<AjusteInventarioResponseDTO> registrarAjuste(
            @Valid @RequestBody AjusteInventarioRequestDTO dto,
            Authentication authentication) {

        AjusteInventarioResponseDTO ajuste =
                ajusteInventarioService.registrarAjuste(
                        dto,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ajuste);
    }

    @GetMapping
    public ResponseEntity<List<AjusteInventarioResponseDTO>> listarAjustes() {

        return ResponseEntity.ok(
                ajusteInventarioService.listarAjustes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AjusteInventarioResponseDTO> obtenerAjuste(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ajusteInventarioService.obtenerAjustePorId(id)
        );
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<AjusteInventarioResponseDTO>>
            listarPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                ajusteInventarioService
                        .listarAjustesPorProducto(idProducto)
        );
    }

    @GetMapping("/existencia/{idExistencia}")
    public ResponseEntity<List<AjusteInventarioResponseDTO>>
            listarPorExistencia(
                    @PathVariable Long idExistencia) {

        return ResponseEntity.ok(
                ajusteInventarioService
                        .listarAjustesPorExistencia(idExistencia)
        );
    }
}