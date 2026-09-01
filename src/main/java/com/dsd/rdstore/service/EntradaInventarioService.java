package com.dsd.rdstore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.dto.entradaInventario.DetalleEntradaInventarioRequestDTO;
import com.dsd.rdstore.dto.entradaInventario.DetalleEntradaInventarioResponseDTO;
import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioRequestDTO;
import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.DetalleEntradaInventario;
import com.dsd.rdstore.model.EntradaInventario;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.repository.DetalleEntradaInventarioRepository;
import com.dsd.rdstore.repository.EntradaInventarioRepository;
import com.dsd.rdstore.repository.ProductoRepository;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de gestionar las entradas de inventario.
 *
 * Registra la cabecera de una entrada y sus productos asociados,
 * genera las existencias correspondientes y actualiza el stock
 * total de cada producto.
 *
 * El registro de una entrada se ejecuta de forma transaccional para
 * garantizar que, si alguna operación falla, ningún cambio parcial
 * quede almacenado en la base de datos.
 */
@Service
@RequiredArgsConstructor
public class EntradaInventarioService {

    private static final String RECURSO = "Entrada de inventario";

    private final EntradaInventarioRepository entradaInventarioRepository;
    private final DetalleEntradaInventarioRepository detalleEntradaInventarioRepository;
    private final ProductoRepository productoRepository;
    private final ExistenciaProductoService existenciaProductoService;
    private final MovimientoInventarioService movimientoInventarioService;


    @Transactional
    public EntradaInventarioResponseDTO registrarEntrada(
            EntradaInventarioRequestDTO dto) {

        EntradaInventario entrada = new EntradaInventario();

        entrada.setFechaEntrada(dto.fechaEntrada());
        entrada.setFechaRegistro(LocalDateTime.now());
        entrada.setNumeroDocumento(
                normalizarTexto(dto.numeroDocumento())
        );
        entrada.setObservacion(
                normalizarTexto(dto.observacion())
        );

        EntradaInventario entradaGuardada =
                entradaInventarioRepository.save(entrada);

        List<DetalleEntradaInventarioResponseDTO> detallesResponse =
                new ArrayList<>();

        for (DetalleEntradaInventarioRequestDTO detalleDto
                : dto.detalles()) {

            Producto producto =
                    obtenerProductoActivoPorId(
                            detalleDto.idProducto()
                    );

            String numeroLote =
                    normalizarTexto(
                            detalleDto.numeroLote()
                    );

            DetalleEntradaInventario detalle =
                    new DetalleEntradaInventario();

            detalle.setEntrada(entradaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.cantidad());
            detalle.setNumeroLote(numeroLote);
            detalle.setFechaVencimiento(
                    detalleDto.fechaVencimiento()
            );

            DetalleEntradaInventario detalleGuardado =
                    detalleEntradaInventarioRepository.save(detalle);

            ExistenciaProducto existencia =
                existenciaProductoService.registrarExistencia(
                        producto,
                        detalleDto.cantidad(),
                        numeroLote,
                        detalleDto.fechaVencimiento()
                );

            movimientoInventarioService.registrarMovimiento(
                    existencia,
                    EnumTipoMovimientoInventario.ENTRADA,
                    detalleDto.cantidad(),
                    EnumTipoOrigenInventario.ENTRADA_INVENTARIO,
                    entradaGuardada.getId(),
                    "Entrada de inventario"
            );

            detallesResponse.add(
                    mapDetalleResponse(detalleGuardado)
            );
        }

        return mapResponse(
                entradaGuardada,
                detallesResponse
        );
    }


    public List<EntradaInventarioResponseDTO> listarEntradas() {

        return entradaInventarioRepository
                .findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public EntradaInventarioResponseDTO obtenerEntradaPorId(Long id) {

        EntradaInventario entrada =
                obtenerEntrada(id);

        return mapResponse(entrada);
    }


    private EntradaInventario obtenerEntrada(Long id) {

        return entradaInventarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RECURSO,
                                id
                        )
                );
    }


    private Producto obtenerProductoActivoPorId(Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto",
                                id
                        )
                );

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new BusinessRuleException(
                    "El producto seleccionado se encuentra inactivo"
            );
        }

        return producto;
    }


    private String normalizarTexto(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }


    private EntradaInventarioResponseDTO mapResponse(
            EntradaInventario entrada) {

        List<DetalleEntradaInventarioResponseDTO> detalles =
                detalleEntradaInventarioRepository
                        .findByEntradaIdOrderByIdAsc(
                                entrada.getId()
                        )
                        .stream()
                        .map(this::mapDetalleResponse)
                        .toList();

        return mapResponse(
                entrada,
                detalles
        );
    }


    private EntradaInventarioResponseDTO mapResponse(
            EntradaInventario entrada,
            List<DetalleEntradaInventarioResponseDTO> detalles) {

        return new EntradaInventarioResponseDTO(
                entrada.getId(),
                entrada.getFechaEntrada(),
                entrada.getFechaRegistro(),
                entrada.getNumeroDocumento(),
                entrada.getObservacion(),
                detalles
        );
    }


    private DetalleEntradaInventarioResponseDTO mapDetalleResponse(
            DetalleEntradaInventario detalle) {

        return new DetalleEntradaInventarioResponseDTO(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getNumeroLote(),
                detalle.getFechaVencimiento()
        );
    }
}