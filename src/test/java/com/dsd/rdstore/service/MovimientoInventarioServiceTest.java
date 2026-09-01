package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.MovimientoInventarioRepository;
import com.dsd.rdstore.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Mock
    private ExistenciaProductoRepository existenciaProductoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private MovimientoInventarioService movimientoInventarioService;


    @Test
    void ajusteSalidaNoDebeAceptarCantidadPositiva() {

        ExistenciaProducto existencia =
                new ExistenciaProducto();

        assertThrows(
                BusinessRuleException.class,
                () -> movimientoInventarioService.registrarMovimiento(
                        existencia,
                        EnumTipoMovimientoInventario.AJUSTE_SALIDA,
                        3L,
                        EnumTipoOrigenInventario.AJUSTE_INVENTARIO,
                        1L,
                        "Producto dañado"
                )
        );

        verify(movimientoInventarioRepository, never())
                .save(any());
    }
}