package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioRequestDTO;
import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioResponseDTO;
import com.dsd.rdstore.service.EntradaInventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado de exponer las operaciones relacionadas
 * con las entradas de inventario.
 *
 * Permite registrar nuevas entradas de mercancía y consultar
 * las entradas previamente registradas junto con sus detalles.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/entradas-inventario")
public class EntradaInventarioController {

    private final EntradaInventarioService entradaInventarioService;

    @PostMapping
    public ResponseEntity<EntradaInventarioResponseDTO> registrarEntrada(
            @Valid @RequestBody EntradaInventarioRequestDTO dto) {

        EntradaInventarioResponseDTO entrada =
                entradaInventarioService.registrarEntrada(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entrada);
    }

    @GetMapping
    public ResponseEntity<List<EntradaInventarioResponseDTO>> listarEntradas() {

        return ResponseEntity.ok(
                entradaInventarioService.listarEntradas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntradaInventarioResponseDTO> obtenerEntrada(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                entradaInventarioService.obtenerEntradaPorId(id)
        );
    }
}