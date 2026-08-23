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
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.producto.ProductoEstadoDTO;
import com.dsd.rdstore.dto.producto.ProductoRequestDTO;
import com.dsd.rdstore.dto.producto.ProductoResponseDTO;
import com.dsd.rdstore.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/productos")
public class ProductoController {

    private final ProductoService productoService;


    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {

        return ResponseEntity.ok(
                productoService.listarProductos()
        );
    }


    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosActivos() {

        return ResponseEntity.ok(
                productoService.listarProductosActivos()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productoService.obtenerProducto(id)
        );
    }


    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO dto) {

        ProductoResponseDTO producto =
                productoService.crearProducto(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(producto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {

        return ResponseEntity.ok(
                productoService.actualizarProducto(id, dto)
        );
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProductoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ProductoEstadoDTO dto) {

        return ResponseEntity.ok(
                productoService.cambiarEstado(id, dto)
        );
    }
}