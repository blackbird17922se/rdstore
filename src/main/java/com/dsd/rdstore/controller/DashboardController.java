package com.dsd.rdstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.dashboard.DashboardResumenDTO;
import com.dsd.rdstore.service.DashboardService;

import lombok.RequiredArgsConstructor;

/** Inicio: indicadores agregados de solo lectura. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> obtenerResumen(
            @RequestParam(defaultValue = "5") int umbralStockBajo,
            Authentication authentication) {
        return ResponseEntity.ok(
                dashboardService.obtenerResumen(umbralStockBajo, authentication));
    }
}
