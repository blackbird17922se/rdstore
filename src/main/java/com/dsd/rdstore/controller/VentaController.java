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
import com.dsd.rdstore.dto.venta.VentaRequestDTO;
import com.dsd.rdstore.dto.venta.VentaResponseDTO;
import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService servicio;

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(servicio.listarVentas());
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> registrarVenta(
        @Valid @RequestBody VentaRequestDTO request, Authentication authentication) {
        servicio.registrarVenta(request, authentication);
        return ResponseEntity.ok(Map.of("mensaje", "Venta registrada correctamente"));
    }

    // @PatchMapping("/{id}/anular")
    // public VentaListadoDTO anular(
    //     @PathVariable Integer id,
    //     @RequestBody AnularVentaRequestDTO dto
    // ) {
    //     return servicio.anularVenta(id, dto.motivo());
    // }

    /* tal vez se use despues para generar borradores y confirmacion venta */
    @PatchMapping("/ventas/{id}/confirmar")
    public Venta confirmar(@PathVariable Integer id) {
        return servicio.confirmarVenta(id);
    }
}
