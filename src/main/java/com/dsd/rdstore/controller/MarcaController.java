package com.dsd.rdstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.marca.MarcaEstadoDTO;
import com.dsd.rdstore.dto.marca.MarcaRequestDTO;
import com.dsd.rdstore.dto.marca.MarcaResponseDTO;
import com.dsd.rdstore.service.MarcaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/marcas")
public class MarcaController {

    private final MarcaService marcaService;


    @GetMapping()
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcas() {
        return ResponseEntity.ok(marcaService.listarMarcas());
    }


    @GetMapping("/activas")
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcasActivas() {
        return ResponseEntity.ok(marcaService.listarMarcasActivas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> obtenerMarcaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.obtenerMarcaPorId(id));
    }


    @PostMapping
    public ResponseEntity<MarcaResponseDTO> crearMarca(
        @Valid @RequestBody MarcaRequestDTO dto) {

            MarcaResponseDTO response = marcaService.crearMarca(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> actualizarMarca(
        @PathVariable Long id, @Valid @RequestBody MarcaRequestDTO dto) {

        return ResponseEntity.ok(marcaService.actualizarMarca(id, dto));
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<MarcaResponseDTO> cambiarEstado(
        @PathVariable Long id, @Valid @RequestBody MarcaEstadoDTO dto){

            return ResponseEntity.ok(marcaService.cambiarEstado(id, dto));
    }
}
