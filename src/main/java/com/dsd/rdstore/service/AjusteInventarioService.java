package com.dsd.rdstore.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioRequestDTO;
import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.AjusteInventario;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.model.enums.TipoAjusteInventario;
import com.dsd.rdstore.model.enums.TipoMovimientoInventario;
import com.dsd.rdstore.model.enums.TipoOrigenInventario;
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

    private final AjusteInventarioRepository ajusteInventarioRepository;
    private final ExistenciaProductoRepository existenciaProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioService movimientoInventarioService;


    @Transactional
    public AjusteInventarioResponseDTO registrarAjuste(
            AjusteInventarioRequestDTO dto,
            String nombreUsuario) {

        ExistenciaProducto existencia =
                obtenerExistenciaPorId(dto.idExistencia());

        Usuario usuario =
                obtenerUsuario(nombreUsuario);

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


    private void aplicarAjuste(
            ExistenciaProducto existencia,
            TipoAjusteInventario tipo,
            Long cantidad) {

        Long cantidadActual = existencia.getCantidad();

        if (tipo == TipoAjusteInventario.ENTRADA) {

            existencia.setCantidad(
                    cantidadActual + cantidad
            );

            return;
        }

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

        TipoMovimientoInventario tipoMovimiento;
        Long cantidadMovimiento;

        if (ajuste.getTipo() == TipoAjusteInventario.ENTRADA) {

            tipoMovimiento =
                    TipoMovimientoInventario.AJUSTE_ENTRADA;

            cantidadMovimiento =
                    ajuste.getCantidad();

        } else {

            tipoMovimiento =
                    TipoMovimientoInventario.AJUSTE_SALIDA;

            cantidadMovimiento =-ajuste.getCantidad(); //ajuste.getCantidad() pasa a val negativo
        }

        movimientoInventarioService.registrarMovimiento(
                existencia,
                tipoMovimiento,
                cantidadMovimiento,
                TipoOrigenInventario.AJUSTE_INVENTARIO,
                ajuste.getId(),
                ajuste.getMotivo()
        );
    }


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

        return usuarioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                nombreUsuario
                        )
                );
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