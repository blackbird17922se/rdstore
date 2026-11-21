package com.dsd.rdstore.controller;

import java.util.List;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.model.enums.EnumTipoAjusteInventario;
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

    /** Historial filtrado en el servidor antes de paginar. */
    @GetMapping("/paginados")
    public ResponseEntity<PageResponseDTO<AjusteInventarioResponseDTO>> listarPaginados(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EnumTipoAjusteInventario tipo,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idExistencia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaAjuste") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion) {

        return ResponseEntity.ok(ajusteInventarioService.listarPaginados(
                texto, tipo, idProducto, idExistencia,
                fechaDesde, fechaHasta, page, size, ordenarPor, direccion));
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
