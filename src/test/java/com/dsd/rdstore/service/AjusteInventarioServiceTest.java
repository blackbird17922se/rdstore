package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioRequestDTO;
import com.dsd.rdstore.dto.ajusteInventario.AjusteInventarioResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.model.AjusteInventario;
import com.dsd.rdstore.model.ExistenciaProducto;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.model.enums.TipoAjusteInventario;
import com.dsd.rdstore.model.enums.TipoMovimientoInventario;
import com.dsd.rdstore.model.enums.TipoOrigenInventario;
import com.dsd.rdstore.repository.AjusteInventarioRepository;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AjusteInventarioServiceTest {

    @Mock
    private AjusteInventarioRepository ajusteInventarioRepository;

    @Mock
    private ExistenciaProductoRepository existenciaProductoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MovimientoInventarioService movimientoInventarioService;

    @InjectMocks
    private AjusteInventarioService ajusteInventarioService;


    @Test
    void ajusteSalidaDebeDisminuirExistenciaYGenerarMovimientoNegativo() {

        ExistenciaProducto existencia = crearExistencia(24L);
        Usuario usuario = crearUsuario();

        AjusteInventarioRequestDTO dto =
                new AjusteInventarioRequestDTO(
                        6L,
                        TipoAjusteInventario.SALIDA,
                        3L,
                        "Producto dañado",
                        "Daño físico"
                );

        when(existenciaProductoRepository.findById(6L))
                .thenReturn(Optional.of(existencia));

        when(usuarioRepository.findByNombreUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        when(ajusteInventarioRepository.save(any(AjusteInventario.class)))
                .thenAnswer(invocacion -> {

                    AjusteInventario ajuste =
                            invocacion.getArgument(0);

                    ajuste.setId(1L);

                    return ajuste;
                });

        AjusteInventarioResponseDTO respuesta =
                ajusteInventarioService.registrarAjuste(
                        dto,
                        "admin"
                );

        assertEquals(21L, existencia.getCantidad());
        assertEquals(3L, respuesta.cantidad());
        assertEquals(TipoAjusteInventario.SALIDA, respuesta.tipo());

        verify(movimientoInventarioService)
                .registrarMovimiento(
                        existencia,
                        TipoMovimientoInventario.AJUSTE_SALIDA,
                        -3L,
                        TipoOrigenInventario.AJUSTE_INVENTARIO,
                        1L,
                        "Producto dañado"
                );
    }


    @Test
    void ajusteSalidaNoDebeSuperarCantidadDisponible() {

        ExistenciaProducto existencia = crearExistencia(10L);
        Usuario usuario = crearUsuario();

        AjusteInventarioRequestDTO dto =
                new AjusteInventarioRequestDTO(
                        6L,
                        TipoAjusteInventario.SALIDA,
                        15L,
                        "Prueba stock insuficiente",
                        null
                );

        when(existenciaProductoRepository.findById(6L))
                .thenReturn(Optional.of(existencia));

        when(usuarioRepository.findByNombreUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                BusinessRuleException.class,
                () -> ajusteInventarioService.registrarAjuste(
                        dto,
                        "admin"
                )
        );

        assertEquals(10L, existencia.getCantidad());

        verify(ajusteInventarioRepository, never())
                .save(any(AjusteInventario.class));

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


    @Test
    void ajusteEntradaDebeIncrementarExistencia() {

        ExistenciaProducto existencia = crearExistencia(10L);
        Usuario usuario = crearUsuario();

        AjusteInventarioRequestDTO dto =
                new AjusteInventarioRequestDTO(
                        6L,
                        TipoAjusteInventario.ENTRADA,
                        5L,
                        "Unidades encontradas",
                        null
                );

        when(existenciaProductoRepository.findById(6L))
                .thenReturn(Optional.of(existencia));

        when(usuarioRepository.findByNombreUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        when(ajusteInventarioRepository.save(any(AjusteInventario.class)))
                .thenAnswer(invocacion -> {

                    AjusteInventario ajuste =
                            invocacion.getArgument(0);

                    ajuste.setId(2L);

                    return ajuste;
                });

        ajusteInventarioService.registrarAjuste(
                dto,
                "admin"
        );

        assertEquals(15L, existencia.getCantidad());

        verify(movimientoInventarioService)
                .registrarMovimiento(
                        existencia,
                        TipoMovimientoInventario.AJUSTE_ENTRADA,
                        5L,
                        TipoOrigenInventario.AJUSTE_INVENTARIO,
                        2L,
                        "Unidades encontradas"
                );
    }


    private ExistenciaProducto crearExistencia(Long cantidad) {

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Cuaderno cuadriculado");

        ExistenciaProducto existencia =
                new ExistenciaProducto();

        existencia.setId(6L);
        existencia.setProducto(producto);
        existencia.setCantidad(cantidad);

        return existencia;
    }


    private Usuario crearUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("admin");

        return usuario;
    }
}