package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dsd.rdstore.dto.presentacion.PresentacionEstadoDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionRequestDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionResponseDTO;
import com.dsd.rdstore.service.PresentacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/presentaciones")
public class PresentacionController {

    private final PresentacionService presentacionService;


    @GetMapping
    public ResponseEntity<List<PresentacionResponseDTO>>
            listarPresentaciones() {

        return ResponseEntity.ok(
            presentacionService.listarPresentaciones()
        );
    }


    @GetMapping("/activas")
    public ResponseEntity<List<PresentacionResponseDTO>>
            listarPresentacionesActivas() {

        return ResponseEntity.ok(
            presentacionService.listarPresentacionesActivas()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO>
            obtenerPresentacionPorId(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
            presentacionService.obtenerPresentacionPorId(id)
        );
    }


    @PostMapping
    public ResponseEntity<PresentacionResponseDTO>
            crearPresentacion(
                    @Valid @RequestBody PresentacionRequestDTO dto) {

        PresentacionResponseDTO response =
            presentacionService.crearPresentacion(dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO>
            actualizarPresentacion(
                    @PathVariable Long id,
                    @Valid @RequestBody PresentacionRequestDTO dto) {

        return ResponseEntity.ok(
            presentacionService.actualizarPresentacion(id, dto)
        );
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<PresentacionResponseDTO>
            cambiarEstado(
                    @PathVariable Long id,
                    @Valid @RequestBody PresentacionEstadoDTO dto) {

        return ResponseEntity.ok(
            presentacionService.cambiarEstado(id, dto)
        );
    }
}