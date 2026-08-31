package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.entradaInventario.DetalleEntradaInventarioRequestDTO;
import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioRequestDTO;
import com.dsd.rdstore.dto.entradaInventario.EntradaInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.model.DetalleEntradaInventario;
import com.dsd.rdstore.model.EntradaInventario;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.repository.DetalleEntradaInventarioRepository;
import com.dsd.rdstore.repository.EntradaInventarioRepository;
import com.dsd.rdstore.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class EntradaInventarioServiceTest {

    @Mock
    private EntradaInventarioRepository entradaInventarioRepository;

    @Mock
    private DetalleEntradaInventarioRepository detalleEntradaInventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ExistenciaProductoService existenciaProductoService;

    @Mock
    private MovimientoInventarioService movimientoInventarioService;

    @InjectMocks
    private EntradaInventarioService entradaInventarioService;


    @Test
    void registrarEntradaDebeCrearDetalleExistenciaYMovimiento() {

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Leche 1L");
        producto.setActivo(true);
        producto.setControlaVencimiento(true);

        LocalDate vencimiento = LocalDate.now().plusMonths(1);

        DetalleEntradaInventarioRequestDTO detalleDto =
                new DetalleEntradaInventarioRequestDTO(
                        10L,
                        24L,
                        "LOTE-001",
                        vencimiento
                );

        EntradaInventarioRequestDTO dto =
                new EntradaInventarioRequestDTO(
                        LocalDate.now(),
                        "FV-001",
                        "Entrada de prueba",
                        List.of(detalleDto)
                );

        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(producto));

        when(entradaInventarioRepository.save(any(EntradaInventario.class)))
                .thenAnswer(invocacion -> {

                    EntradaInventario entrada =
                            invocacion.getArgument(0);

                    entrada.setId(1L);

                    return entrada;
                });

        when(detalleEntradaInventarioRepository
                .save(any(DetalleEntradaInventario.class)))
                .thenAnswer(invocacion -> {

                    DetalleEntradaInventario detalle =
                            invocacion.getArgument(0);

                    detalle.setId(1L);

                    return detalle;
                });

        ExistenciaProducto existencia = new ExistenciaProducto();
        existencia.setId(5L);
        existencia.setProducto(producto);
        existencia.setCantidad(24L);
        existencia.setNumeroLote("LOTE-001");
        existencia.setFechaVencimiento(vencimiento);

        when(existenciaProductoService.registrarExistencia(
                producto,
                24L,
                "LOTE-001",
                vencimiento
        )).thenReturn(existencia);

        EntradaInventarioResponseDTO respuesta =
                entradaInventarioService.registrarEntrada(dto);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(1, respuesta.detalles().size());
        assertEquals(24L, respuesta.detalles().get(0).cantidad());

        verify(existenciaProductoService)
                .registrarExistencia(
                        producto,
                        24L,
                        "LOTE-001",
                        vencimiento
                );

        verify(movimientoInventarioService)
                .registrarMovimiento(
                        existencia,
                        EnumTipoMovimientoInventario.ENTRADA,
                        24L,
                        EnumTipoOrigenInventario.ENTRADA_INVENTARIO,
                        1L,
                        "Entrada de inventario"
                );
    }


    @Test
    void registrarEntradaDebeRechazarProductoInactivo() {

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Producto inactivo");
        producto.setActivo(false);

        DetalleEntradaInventarioRequestDTO detalleDto =
                new DetalleEntradaInventarioRequestDTO(
                        10L,
                        10L,
                        null,
                        null
                );

        EntradaInventarioRequestDTO dto =
                new EntradaInventarioRequestDTO(
                        LocalDate.now(),
                        null,
                        null,
                        List.of(detalleDto)
                );

        when(entradaInventarioRepository.save(any(EntradaInventario.class)))
                .thenAnswer(invocacion -> {

                    EntradaInventario entrada =
                            invocacion.getArgument(0);

                    entrada.setId(1L);

                    return entrada;
                });

        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                BusinessRuleException.class,
                () -> entradaInventarioService.registrarEntrada(dto)
        );

        verify(existenciaProductoService, never())
                .registrarExistencia(
                        any(),
                        any(),
                        any(),
                        any()
                );

        verify(movimientoInventarioService, never())
                .registrarMovimiento(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                );
    }
}