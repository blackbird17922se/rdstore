package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dsd.rdstore.dto.categoria.CategoriaEstadoDTO;
import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/paginadas")
    public ResponseEntity<PageResponseDTO<CategoriaResponseDTO>> listarPaginadas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {
        try {
            return ResponseEntity.ok(categoriaService.buscarPaginadas(
                    nombre, activo, page, size, ordenarPor, direccion));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategorias(
        @Valid @RequestBody CategoriaRequestDTO dto
    ){
        CategoriaResponseDTO nuevaCategoria = categoriaService.crearCategorias(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias(){
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
        @PathVariable Long id, 
        @Valid @RequestBody CategoriaRequestDTO dto
    ){
        CategoriaResponseDTO categoria = categoriaService.actualizarCategoria(id, dto);

        return ResponseEntity.ok(categoria);

    }


    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarActivas() {
        return ResponseEntity.ok(
                categoriaService.listarActivas()
        );
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<CategoriaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CategoriaEstadoDTO activo) {

        return ResponseEntity.ok(
                categoriaService.cambiarEstado(id, activo)
        );
    }

}
