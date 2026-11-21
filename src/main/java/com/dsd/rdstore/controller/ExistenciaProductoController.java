package com.dsd.rdstore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.existencia.ExistenciaProductoResponseDTO;
import com.dsd.rdstore.dto.vencimiento.AlertaVencimientoResponseDTO;
import com.dsd.rdstore.dto.vencimiento.ResumenVencimientosDTO;
import com.dsd.rdstore.model.enums.EnumEstadoVencimiento;
import com.dsd.rdstore.service.ExistenciaProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/existencias")
public class ExistenciaProductoController {

    private final ExistenciaProductoService existenciaProductoService;


    @GetMapping
    public ResponseEntity<List<ExistenciaProductoResponseDTO>> 
        listarExistencias() {
                return ResponseEntity.ok(
                        existenciaProductoService.listarExistencias());
    }
    


    /** Consulta paginada para la pantalla de Existencias (solo lectura). */
    @GetMapping("/paginados")
    public ResponseEntity<PageResponseDTO<ExistenciaProductoResponseDTO>> listarPaginados(
        @RequestParam(required = false) String texto,
        @RequestParam(required = false) Boolean conStock,
        @RequestParam(required = false) Long idProducto,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "fechaIngreso") String ordenarPor,
        @RequestParam(defaultValue = "desc") String direccion,
        @RequestParam(required = false) String numeroLote,
        @RequestParam(required = false) Long cantidadMin,
        @RequestParam(required = false) Long cantidadMax,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate vencimientoDesde,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate vencimientoHasta,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate ingresoDesde,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate ingresoHasta ) {

        return ResponseEntity.ok(
                existenciaProductoService.listarExistenciasPaginadas(
                        texto, conStock, idProducto, page, size, ordenarPor, direccion,
                        numeroLote, cantidadMin, cantidadMax,
                        vencimientoDesde, vencimientoHasta, ingresoDesde, ingresoHasta));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ExistenciaProductoResponseDTO>
            obtenerExistencia(@PathVariable Long id) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .obtenerExistenciaPorId(id)
        );
    }


    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarExistenciasPorProducto(idProducto)
        );
    }


    @GetMapping("/producto/{idProducto}/disponibles")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarDisponiblesPorProducto(
                    @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarExistenciasDisponiblesPorProducto(
                                idProducto
                        )
        );
    }


    @GetMapping("/proximas-vencer")
    public ResponseEntity<List<ExistenciaProductoResponseDTO>>
            listarProximosAVencer(
                    @RequestParam(defaultValue = "30")
                    Integer dias) {

        return ResponseEntity.ok(
                existenciaProductoService
                        .listarProximosAVencer(dias)
        );
    }


    /**
     * Alertas paginadas por lote. Incluye vencidos, pero nunca existencias agotadas.
     * dias delimita el horizonte futuro; los lotes vencidos siempre están incluidos.
     * La combinación estado + dias aplica ambos filtros (intersección).
     */
    @GetMapping("/proximas-vencer/paginados")
    public ResponseEntity<PageResponseDTO<AlertaVencimientoResponseDTO>> listarAlertasVencimiento(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) EnumEstadoVencimiento estado,
            @RequestParam(defaultValue = "30") Integer dias,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimientoDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimientoHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaVencimiento") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {

        return ResponseEntity.ok(
                existenciaProductoService.listarAlertasVencimiento(
                        texto, idProducto, estado, dias,
                        vencimientoDesde, vencimientoHasta,
                        page, size, ordenarPor, direccion));
    }

    /**
     * Contadores por estado sobre todos los lotes con stock y vencimiento.
     * No aplica el horizonte 'dias' para conservar el total de cada estado.
     * Admite la búsqueda y el producto seleccionados para mantener contexto.
     */
    @GetMapping("/proximas-vencer/resumen")
    public ResponseEntity<ResumenVencimientosDTO> resumenVencimientos(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long idProducto) {
        return ResponseEntity.ok(
                existenciaProductoService.resumenVencimientos(texto, idProducto));
    }
}