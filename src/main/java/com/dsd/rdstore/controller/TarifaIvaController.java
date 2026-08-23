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

import com.dsd.rdstore.dto.tarifaiva.TarifaIvaEstadoDTO;
import com.dsd.rdstore.dto.tarifaiva.TarifaIvaRequestDTO;
import com.dsd.rdstore.dto.tarifaiva.TarifaIvaResponseDTO;
import com.dsd.rdstore.service.TarifaIvaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/tarifas-iva")
public class TarifaIvaController {

    private final TarifaIvaService tarifaIvaService;


    @GetMapping
    public ResponseEntity<List<TarifaIvaResponseDTO>>
            listarTarifas() {

        return ResponseEntity.ok(
            tarifaIvaService.listarTarifas()
        );
    }


    @GetMapping("/activas")
    public ResponseEntity<List<TarifaIvaResponseDTO>>
            listarTarifasActivas() {

        return ResponseEntity.ok(
            tarifaIvaService.listarTarifasActivas()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<TarifaIvaResponseDTO>
            obtenerTarifaPorId(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
            tarifaIvaService.obtenerTarifaPorId(id)
        );
    }


    @PostMapping
    public ResponseEntity<TarifaIvaResponseDTO>
            crearTarifa(
                    @Valid
                    @RequestBody TarifaIvaRequestDTO dto) {

        TarifaIvaResponseDTO response =
            tarifaIvaService.crearTarifa(dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TarifaIvaResponseDTO>
            actualizarTarifa(
                    @PathVariable Long id,
                    @Valid
                    @RequestBody TarifaIvaRequestDTO dto) {

        return ResponseEntity.ok(
            tarifaIvaService.actualizarTarifa(id, dto)
        );
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<TarifaIvaResponseDTO>
            cambiarEstado(
                    @PathVariable Long id,
                    @Valid
                    @RequestBody TarifaIvaEstadoDTO dto) {

        return ResponseEntity.ok(
            tarifaIvaService.cambiarEstado(id, dto)
        );
    }
}