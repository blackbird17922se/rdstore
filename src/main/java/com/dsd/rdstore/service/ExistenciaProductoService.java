package com.dsd.rdstore.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.common.util.SpecificationPageUtils;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.existencia.ExistenciaProductoResponseDTO;
import com.dsd.rdstore.dto.vencimiento.AlertaVencimientoResponseDTO;
import com.dsd.rdstore.dto.vencimiento.ResumenVencimientosDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.ProductoRepository;
import com.dsd.rdstore.specification.ExistenciaProductoSpecification;
import com.dsd.rdstore.specification.AlertaVencimientoSpecification;
import com.dsd.rdstore.model.enums.EnumEstadoVencimiento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExistenciaProductoService {

    private final ExistenciaProductoRepository existenciaProductoRepository;
    private final ProductoRepository productoRepository;

    // Solo campos de la entidad autorizados para ordenar el listado administrativo.
    private static final Set<String> CAMPOS_ORDEN = Set.of(
            "id", "producto.nombre", "cantidad", "numeroLote",
            "fechaVencimiento", "fechaIngreso");


    
    public List<ExistenciaProductoResponseDTO> listarExistencias() {

        return existenciaProductoRepository
                .findAllByOrderByFechaIngresoDesc()
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    /**
     * Consulta administrativa paginada por registro/lote.
     * No modifica cantidades ni las reglas de selección FEFO/FIFO.
     * conStock = null: todos; true: cantidad > 0; false: cantidad <= 0.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ExistenciaProductoResponseDTO> listarExistenciasPaginadas(
            String texto,
            Boolean conStock,
            Long idProducto,
            int page,
            int size,
            String ordenarPor,
            String direccion,
            String numeroLote,
            Long cantidadMin,
            Long cantidadMax,
            LocalDate vencimientoDesde,
            LocalDate vencimientoHasta,
            LocalDate ingresoDesde,
            LocalDate ingresoHasta) {

        return SpecificationPageUtils.buscar(
                existenciaProductoRepository,
                ExistenciaProductoSpecification.filtrar(
                        texto, conStock, idProducto,
                        numeroLote, cantidadMin, cantidadMax,
                        vencimientoDesde, vencimientoHasta, ingresoDesde, ingresoHasta),
                this::mapResponse,
                page,
                size,
                ordenarPor,
                direccion,
                CAMPOS_ORDEN);
    }


    public List<ExistenciaProductoResponseDTO>
            listarExistenciasPorProducto(Long idProducto) {

        obtenerProductoPorId(idProducto);

        return existenciaProductoRepository
                .findByProductoIdOrderByFechaIngresoAsc(idProducto)
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<ExistenciaProductoResponseDTO>
            listarExistenciasDisponiblesPorProducto(Long idProducto) {

        obtenerProductoPorId(idProducto);

        return existenciaProductoRepository
                .findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(
                        idProducto,
                        0L
                )
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<ExistenciaProductoResponseDTO>
            listarProximosAVencer(Integer dias) {

        if (dias == null || dias <= 0) {
            throw new BusinessRuleException(
                    "La cantidad de días debe ser mayor a cero"
            );
        }

        LocalDate fechaInicial = LocalDate.now();
        LocalDate fechaFinal = fechaInicial.plusDays(dias);

        return existenciaProductoRepository
                .findByFechaVencimientoBetweenAndCantidadGreaterThanOrderByFechaVencimientoAsc(
                        fechaInicial,
                        fechaFinal,
                        0L
                )
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public ExistenciaProductoResponseDTO
            obtenerExistenciaPorId(Long id) {

        return mapResponse(
                obtenerExistencia(id)
        );
    }


    /*
     * Este método NO será expuesto directamente mediante un POST.
     * Será utilizado posteriormente por EntradaInventarioService.
     */
    public ExistenciaProducto registrarExistencia(
            Producto producto,
            Long cantidad,
            String numeroLote,
            LocalDate fechaVencimiento) {

        validarNuevaExistencia(
                producto,
                cantidad,
                fechaVencimiento
        );

        ExistenciaProducto existencia =
                new ExistenciaProducto();

        existencia.setProducto(producto);
        existencia.setCantidad(cantidad);
        existencia.setNumeroLote(
                normalizarNumeroLote(numeroLote)
        );
        existencia.setFechaVencimiento(fechaVencimiento);
        existencia.setFechaIngreso(LocalDateTime.now());

        return existenciaProductoRepository.save(existencia);
    }


    private void validarNuevaExistencia(
            Producto producto,
            Long cantidad,
            LocalDate fechaVencimiento) {

        if (cantidad == null || cantidad <= 0) {
            throw new BusinessRuleException(
                    "La cantidad de la existencia debe ser mayor a cero"
            );
        }

        if (Boolean.TRUE.equals(producto.getControlaVencimiento())
                && fechaVencimiento == null) {

            throw new BusinessRuleException(
                    "El producto requiere fecha de vencimiento"
            );
        }

        if (fechaVencimiento != null
                && fechaVencimiento.isBefore(LocalDate.now())) {

            throw new BusinessRuleException(
                    "La fecha de vencimiento no puede ser anterior a la fecha actual"
            );
        }
    }


    private String normalizarNumeroLote(String numeroLote) {

        if (numeroLote == null || numeroLote.isBlank()) {
            return null;
        }

        return numeroLote.trim();
    }


    private Producto obtenerProductoPorId(Long id) {

        return productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto",
                                id
                        )
                );
    }


    private ExistenciaProducto obtenerExistencia(Long id) {

        return existenciaProductoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Existencia de producto",
                                id
                        )
                );
    }


    private ExistenciaProductoResponseDTO mapResponse(
            ExistenciaProducto existencia) {

        return new ExistenciaProductoResponseDTO(
                existencia.getId(),

                existencia.getProducto().getId(),
                existencia.getProducto().getNombre(),

                existencia.getCantidad(),

                existencia.getNumeroLote(),
                existencia.getFechaVencimiento(),
                existencia.getFechaIngreso()
        );
    }


    // Ordenamiento restringido a propiedades conocidas de ExistenciaProducto.
    private static final Set<String> CAMPOS_ORDEN_ALERTAS = Set.of(
            "id", "producto.nombre", "cantidad", "numeroLote", "fechaVencimiento", "fechaIngreso");

    /**
     * Consulta de alertas, independiente del listado de Existencias.
     * Solo lee lotes con stock positivo y fecha de vencimiento.
     * No modifica cantidades ni afecta la selección FEFO/FIFO.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<AlertaVencimientoResponseDTO> listarAlertasVencimiento(
            String texto,
            Long idProducto,
            EnumEstadoVencimiento estado,
            Integer dias,
            LocalDate vencimientoDesde,
            LocalDate vencimientoHasta,
            int page,
            int size,
            String ordenarPor,
            String direccion) {

        if (dias == null || dias < 1 || dias > 3650) {
            throw new BusinessRuleException("El horizonte debe estar entre 1 y 3650 días");
        }
        if (vencimientoDesde != null && vencimientoHasta != null
                && vencimientoDesde.isAfter(vencimientoHasta)) {
            throw new BusinessRuleException("La fecha inicial no puede superar la fecha final");
        }

        // La fecha de referencia se captura una sola vez por solicitud.
        LocalDate hoy = LocalDate.now();

        return SpecificationPageUtils.buscar(
                existenciaProductoRepository,
                AlertaVencimientoSpecification.filtrar(
                        texto, idProducto, estado, dias,
                        vencimientoDesde, vencimientoHasta, hoy),
                existencia -> mapAlertaVencimiento(existencia, hoy),
                page, size, ordenarPor, direccion,
                CAMPOS_ORDEN_ALERTAS);
    }

    /**
     * Contadores globales por estado (en la selección de producto/texto),
     * independientes del horizonte del listado. Cuenta lotes, no unidades.
     */
    @Transactional(readOnly = true)
    public ResumenVencimientosDTO resumenVencimientos(String texto, Long idProducto) {
        LocalDate hoy = LocalDate.now();
        long vencidos = contarLotesPorEstado(texto, idProducto, EnumEstadoVencimiento.VENCIDO, hoy);
        long criticos = contarLotesPorEstado(texto, idProducto, EnumEstadoVencimiento.CRITICO, hoy);
        long proximos = contarLotesPorEstado(texto, idProducto, EnumEstadoVencimiento.PROXIMO, hoy);
        long seguimiento = contarLotesPorEstado(texto, idProducto, EnumEstadoVencimiento.SEGUIMIENTO, hoy);
        long sinAlerta = contarLotesPorEstado(texto, idProducto, EnumEstadoVencimiento.SIN_ALERTA, hoy);
        return new ResumenVencimientosDTO(vencidos, criticos, proximos, seguimiento, sinAlerta);
    }

    private long contarLotesPorEstado(
            String texto, Long idProducto, EnumEstadoVencimiento estado, LocalDate hoy) {
        return existenciaProductoRepository.count(
                AlertaVencimientoSpecification.filtrar(
                        texto, idProducto, estado, null,
                        null, null, hoy));
    }

    private AlertaVencimientoResponseDTO mapAlertaVencimiento(
            ExistenciaProducto existencia, LocalDate hoy) {
        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                hoy, existencia.getFechaVencimiento());
        return new AlertaVencimientoResponseDTO(
                existencia.getId(),
                existencia.getProducto().getId(),
                existencia.getProducto().getNombre(),
                existencia.getCantidad(),
                existencia.getNumeroLote(),
                existencia.getFechaVencimiento(),
                diasRestantes,
                EnumEstadoVencimiento.desdeDias(diasRestantes));
    }
}