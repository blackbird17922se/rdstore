package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.producto.ProductoRequestDTO;
import com.dsd.rdstore.dto.producto.ProductoResponseDTO;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.model.Marca;
import com.dsd.rdstore.model.Presentacion;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.enums.TipoIva;
import com.dsd.rdstore.repository.CategoriaRepository;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.MarcaRepository;
import com.dsd.rdstore.repository.PresentacionRepository;
import com.dsd.rdstore.repository.ProductoRepository;
import com.dsd.rdstore.repository.TarifaIvaRepository;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private PresentacionRepository presentacionRepository;
    @Mock
    private MarcaRepository marcaRepository;
    @Mock
    private TarifaIvaRepository tarifaIvaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ExistenciaProductoRepository existenciaProductoRepository;


    @InjectMocks
    private ProductoService productoService;

    private static final String NOMBRE = "Lapiz h2";

    @Test
    void crearProductoDebeGuardarCorrectamente(){
        
        ProductoRequestDTO dto = new ProductoRequestDTO(
            "978855221", NOMBRE, "Lapiz amarillo",
            BigDecimal.valueOf(1500.2), 1L, 1L, 1L, 1L, false);



        Presentacion presentacion = new Presentacion();
        presentacion.setId(1L);
        presentacion.setNombre("CAJA");
        presentacion.setActivo(true);

        Marca marca = new Marca();
        marca.setId(1L);
        marca.setNombre("Paper Mate");
        marca.setActivo(true);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Papeleria");

        TarifaIva tarifaIva = new TarifaIva();
        tarifaIva.setId(1L);
        tarifaIva.setNombre("PruebaTarifa");
        tarifaIva.setPorcentaje(BigDecimal.valueOf(10));
        tarifaIva.setTipo(TipoIva.GRAVADO);
        tarifaIva.setActivo(true);

        when(productoRepository.existsByCodigoBarras(dto.codigoBarras()))
            .thenReturn(false);
        when(presentacionRepository.findById(dto.idPresentacion()))
            .thenReturn(Optional.of(presentacion));
        when(marcaRepository.findById(dto.idMarca()))
            .thenReturn(Optional.of(marca));
        when(categoriaRepository.findById(dto.idCategoria()))
            .thenReturn(Optional.of(categoria));
        when(tarifaIvaRepository.findById(dto.idTarifaIva()))
            .thenReturn(Optional.of(tarifaIva));
        when(existenciaProductoRepository.obtenerStockTotalPorProducto(1L))
            .thenReturn(0L); // porque aun no hay existencias del producto


        when(productoRepository.save(any(Producto.class)))
            .thenAnswer(invocacion -> {
                Producto producto = invocacion.getArgument(0);

                producto.setId(1L);
                return producto;
            }
        );

        ProductoResponseDTO respuesta = productoService.crearProducto(dto);

        assertEquals(1L, respuesta.id());
        assertEquals(NOMBRE, respuesta.nombre());
        assertTrue(respuesta.activo());
        assertEquals(1L, respuesta.idCategoria());
        assertEquals(0L, respuesta.stock());

        verify(productoRepository)
            .existsByCodigoBarras(dto.codigoBarras());
        verify(productoRepository).save(any(Producto.class));
            
    }
    
}
