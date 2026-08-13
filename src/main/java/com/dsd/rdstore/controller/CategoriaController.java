package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/categorias")
public class CategoriaController {

    private final CategoriaService service;


    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarcategoria(){
        return ResponseEntity.ok(service.listarcategoria());
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
        @Valid @RequestBody CategoriaRequestDTO dto){

            CategoriaResponseDTO nuevaCategoria = service.crearCategoria(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);

        }

    
}
