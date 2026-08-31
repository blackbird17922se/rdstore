package com.dsd.rdstore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.venta.AnularVentaRequestDTO;
import com.dsd.rdstore.dto.venta.VentaDetalleDTO;
import com.dsd.rdstore.dto.venta.VentaRequestDTO;
import com.dsd.rdstore.dto.venta.VentaResponseDTO;
import com.dsd.rdstore.service.DetalleVentaService;
import com.dsd.rdstore.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;
    private final DetalleVentaService detalleVentaService;

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> registrarVenta(
        @Valid @RequestBody VentaRequestDTO request, Authentication authentication) {
        ventaService.registrarVenta(request, authentication);
        return ResponseEntity.ok(Map.of("mensaje", "Venta registrada correctamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDetalleDTO> obtenerDetalleVenta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                detalleVentaService.obtenerDetalleVenta(id)
        );
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Map<String, String>> anularVenta(
            @PathVariable Long id,
            @RequestBody AnularVentaRequestDTO request) {

        ventaService.anularVenta(id, request.motivo());

        return ResponseEntity.ok(
                Map.of("mensaje", "Venta anulada correctamente")
        );
    }

}
