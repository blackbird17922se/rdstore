package com.dsd.rdstore.controller;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.model.enums.EnumEstadoVenta;
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


    /** Historial de ventas: filtros y paginación exclusivamente del lado del servidor. */
    @GetMapping("/paginados")
    public ResponseEntity<PageResponseDTO<VentaResponseDTO>> listarPaginados(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EnumEstadoVenta estado,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idVendedor,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) BigDecimal totalMin,
            @RequestParam(required = false) BigDecimal totalMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion) {

        return ResponseEntity.ok(ventaService.listarVentasPaginadas(
                texto, estado, idCliente, idVendedor,
                fechaDesde, fechaHasta, totalMin, totalMax,
                page, size, ordenarPor, direccion));
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
