package com.dsd.rdstore.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.common.util.SpecificationPageUtils;
import com.dsd.rdstore.specification.AjusteInventarioSpecification;
import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioRequestDTO;
import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.AjusteInventario;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.model.enums.EnumTipoAjusteInventario;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.repository.AjusteInventarioRepository;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de gestionar los ajustes manuales realizados
 * sobre existencias específicas del inventario.
 *
 * Permite incrementar o disminuir la cantidad disponible de una
 * existencia, conservando el motivo, la fecha y el usuario responsable
 * de la operación.
 *
 * Cada ajuste genera automáticamente un movimiento de inventario y se
 * ejecuta dentro de una transacción para garantizar la consistencia
 * entre el ajuste, la existencia y su historial de movimientos.
 */
@Service
@RequiredArgsConstructor
public class AjusteInventarioService {

    private static final String RECURSO = "Ajuste de inventario";

    // Campos autorizados para ordenar; nunca se acepta una propiedad arbitraria.
    private static final Set<String> CAMPOS_ORDEN = Set.of(
            "id", "fechaAjuste", "existencia.producto.nombre", "tipo", "cantidad",
            "existencia.id", "usuario.nombreUsuario", "motivo");

    private final AjusteInventarioRepository ajusteInventarioRepository;
    private final ExistenciaProductoRepository existenciaProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioService movimientoInventarioService;


    @Transactional
    public AjusteInventarioResponseDTO registrarAjuste(
            AjusteInventarioRequestDTO dto,
            String nombreUsuario) {

        // Las anotaciones del DTO validan solicitudes HTTP; el servicio también
        // valida para proteger llamadas internas que no pasan por el Controller.
        validarDatosAjuste(dto);

        Usuario usuario =
                obtenerUsuario(nombreUsuario);

        // Bloquea esta EXISTENCIA (lote) hasta finalizar la transacción.
        // La cantidad se comprueba nuevamente con el valor actualizado.
        ExistenciaProducto existencia =
                obtenerExistenciaPorIdParaAjuste(dto.idExistencia());

        aplicarAjuste(
                existencia,
                dto.tipo(),
                dto.cantidad()
        );

        AjusteInventario ajuste =
                new AjusteInventario();

        ajuste.setExistencia(existencia);
        ajuste.setUsuario(usuario);
        ajuste.setTipo(dto.tipo());
        ajuste.setCantidad(dto.cantidad());
        ajuste.setMotivo(dto.motivo().trim());
        ajuste.setObservacion(
                normalizarTexto(dto.observacion())
        );
        ajuste.setFechaAjuste(LocalDateTime.now());

        AjusteInventario ajusteGuardado =
                ajusteInventarioRepository.save(ajuste);

        registrarMovimiento(
                existencia,
                ajusteGuardado
        );

        return mapResponse(ajusteGuardado);
    }


    /** Listado histórico paginado. Solo lectura; no cambia el registro de ajustes. */
    @Transactional(readOnly = true)
    public PageResponseDTO<AjusteInventarioResponseDTO> listarPaginados(
            String texto, EnumTipoAjusteInventario tipo, Long idProducto,
            Long idExistencia, LocalDate fechaDesde, LocalDate fechaHasta,
            int page, int size, String ordenarPor, String direccion) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new BusinessRuleException("La fecha inicial no puede superar la fecha final");
        }
        return SpecificationPageUtils.buscar(
                ajusteInventarioRepository,
                AjusteInventarioSpecification.filtrar(
                        texto, tipo, idProducto, idExistencia, fechaDesde, fechaHasta),
                this::mapResponse,
                page, size, ordenarPor, direccion, CAMPOS_ORDEN);
    }

    public List<AjusteInventarioResponseDTO> listarAjustes() {

        return ajusteInventarioRepository
                .findAllByOrderByFechaAjusteDesc()
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<AjusteInventarioResponseDTO>
            listarAjustesPorProducto(Long idProducto) {

        return ajusteInventarioRepository
                .findByExistenciaProductoIdOrderByFechaAjusteDesc(
                        idProducto
                )
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<AjusteInventarioResponseDTO>
            listarAjustesPorExistencia(Long idExistencia) {

        obtenerExistenciaPorId(idExistencia);

        return ajusteInventarioRepository
                .findByExistenciaIdOrderByFechaAjusteDesc(
                        idExistencia
                )
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public AjusteInventarioResponseDTO obtenerAjustePorId(Long id) {

        AjusteInventario ajuste =
                ajusteInventarioRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        RECURSO,
                                        id
                                )
                        );

        return mapResponse(ajuste);
    }


    /**
     * Protección adicional para invocaciones internas del servicio.
     * En solicitudes HTTP, estas reglas también están declaradas en el DTO.
     */
    private void validarDatosAjuste(AjusteInventarioRequestDTO dto) {

        if (dto == null) {
            throw new BusinessRuleException("Los datos del ajuste son obligatorios");
        }
        if (dto.idExistencia() == null || dto.idExistencia() <= 0) {
            throw new BusinessRuleException("La existencia es obligatoria y debe ser válida");
        }
        if (dto.tipo() == null) {
            throw new BusinessRuleException("El tipo de ajuste es obligatorio");
        }
        if (dto.cantidad() == null || dto.cantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a cero");
        }
        if (dto.motivo() == null || dto.motivo().isBlank()) {
            throw new BusinessRuleException("El motivo del ajuste es obligatorio");
        }
        if (dto.motivo().length() > 150) {
            throw new BusinessRuleException("El motivo no puede superar los 150 caracteres");
        }
        if (dto.observacion() != null && dto.observacion().length() > 500) {
            throw new BusinessRuleException("La observación no puede superar los 500 caracteres");
        }
    }


    private void aplicarAjuste(
            ExistenciaProducto existencia,
            EnumTipoAjusteInventario tipo,
            Long cantidad) {

        Long cantidadActual = existencia.getCantidad();

        // El stock nunca debe ser nulo o negativo antes de ajustar.
        if (cantidadActual == null || cantidadActual < 0) {
            throw new BusinessRuleException(
                    "La existencia presenta una cantidad inválida"
            );
        }

        if (tipo == EnumTipoAjusteInventario.ENTRADA) {

            // Evita que la suma exceda Long.MAX_VALUE y genere stock negativo.
            try {
                existencia.setCantidad(
                        Math.addExact(cantidadActual, cantidad)
                );
            } catch (ArithmeticException ex) {
                throw new BusinessRuleException(
                        "La cantidad resultante supera el máximo permitido"
                );
            }

            return;
        }

        // Salida: comprueba el stock obtenido después de adquirir el bloqueo.
        if (cantidad > cantidadActual) {

            throw new BusinessRuleException(
                    "La cantidad del ajuste de salida supera "
                    + "la cantidad disponible en la existencia"
            );
        }

        existencia.setCantidad(
                cantidadActual - cantidad
        );
    }


    private void registrarMovimiento(
            ExistenciaProducto existencia,
            AjusteInventario ajuste) {

        EnumTipoMovimientoInventario tipoMovimiento;
        Long cantidadMovimiento;

        if (ajuste.getTipo() == EnumTipoAjusteInventario.ENTRADA) {

            tipoMovimiento =
                    EnumTipoMovimientoInventario.AJUSTE_ENTRADA;

            cantidadMovimiento =
                    ajuste.getCantidad();

        } else {

            tipoMovimiento =
                    EnumTipoMovimientoInventario.AJUSTE_SALIDA;

            cantidadMovimiento =-ajuste.getCantidad(); //ajuste.getCantidad() pasa a val negativo
        }

        movimientoInventarioService.registrarMovimiento(
                existencia,
                tipoMovimiento,
                cantidadMovimiento,
                EnumTipoOrigenInventario.AJUSTE_INVENTARIO,
                ajuste.getId(),
                ajuste.getMotivo()
        );
    }


    /**
     * Obtiene y bloquea la fila de la existencia para un ajuste manual.
     * Debe invocarse dentro de registrarAjuste(), que es @Transactional.
     */
    private ExistenciaProducto obtenerExistenciaPorIdParaAjuste(Long id) {

        return existenciaProductoRepository
                .obtenerPorIdConBloqueo(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Existencia de producto",
                                id
                        )
                );
    }


    // Consulta normal: mantiene las lecturas sin bloqueo pesimista.
    private ExistenciaProducto obtenerExistenciaPorId(Long id) {

        return existenciaProductoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Existencia de producto",
                                id
                        )
                );
    }


    private Usuario obtenerUsuario(String nombreUsuario) {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new BusinessRuleException(
                    "No se identificó al usuario responsable del ajuste"
            );
        }

        Usuario usuario = usuarioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                nombreUsuario
                        )
                );

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessRuleException(
                    "El usuario responsable del ajuste está inactivo"
            );
        }

        return usuario;
    }


    private String normalizarTexto(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }


    private AjusteInventarioResponseDTO mapResponse(
            AjusteInventario ajuste) {

        ExistenciaProducto existencia =
                ajuste.getExistencia();

        return new AjusteInventarioResponseDTO(
                ajuste.getId(),

                existencia.getId(),

                existencia.getProducto().getId(),
                existencia.getProducto().getNombre(),

                existencia.getNumeroLote(),
                existencia.getFechaVencimiento(),

                ajuste.getTipo(),
                ajuste.getCantidad(),

                ajuste.getMotivo(),
                ajuste.getObservacion(),

                ajuste.getFechaAjuste(),

                ajuste.getUsuario().getId(),
                ajuste.getUsuario().getNombreUsuario()
        );
    }
}
