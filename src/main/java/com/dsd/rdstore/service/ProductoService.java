package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.producto.ProductoEstadoDTO;
import com.dsd.rdstore.dto.producto.ProductoRequestDTO;
import com.dsd.rdstore.dto.producto.ProductoResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.model.Marca;
import com.dsd.rdstore.model.Presentacion;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.repository.CategoriaRepository;
import com.dsd.rdstore.repository.ExistenciaProductoRepository;
import com.dsd.rdstore.repository.MarcaRepository;
import com.dsd.rdstore.repository.PresentacionRepository;
import com.dsd.rdstore.repository.ProductoRepository;
import com.dsd.rdstore.repository.TarifaIvaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final PresentacionRepository presentacionRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarifaIvaRepository tarifaIvaRepository;
    private final ExistenciaProductoRepository existenciaProductoRepository;

    private static final String RECURSO = "Producto";


    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {

        validarCodigoBarrasCreacion(dto.codigoBarras());

        Producto producto = crearEntidadDesdeDto(dto);

        Producto guardado = productoRepository.save(producto);

        return mapResponse(guardado);

    }


    public List<ProductoResponseDTO> listarProductos() {

        return productoRepository
                .findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public List<ProductoResponseDTO> listarProductosActivos() {

        return productoRepository
                .findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::mapResponse)
                .toList();
    }


    public ProductoResponseDTO actualizarProducto(
            Long id, ProductoRequestDTO dto) {

        Producto producto = obtenerProductoPorId(id);

        validarCodigoBarrasActualizacion(
                dto.codigoBarras(),
                id);

        actualizarDatosProducto(producto, dto);

        Producto guardado = productoRepository.save(producto);

        return mapResponse(guardado);
    }


    public ProductoResponseDTO cambiarEstado(
            Long id,
            ProductoEstadoDTO dto) {

        Producto producto = obtenerProductoPorId(id);

        producto.setActivo(dto.activo());

        Producto actualizada = productoRepository.save(producto);

        return mapResponse(actualizada);
    }


    private ProductoResponseDTO mapResponse(Producto producto) {

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getCodigoBarras(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),

                producto.getMarca().getId(),
                producto.getMarca().getNombre(),

                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),

                producto.getPresentacion().getId(),
                producto.getPresentacion().getNombre(),

                producto.getTarifaIva().getId(),
                producto.getTarifaIva().getNombre(),
                producto.getTarifaIva().getPorcentaje(),

                existenciaProductoRepository
                .obtenerStockTotalPorProducto(producto.getId()),
                producto.getActivo(),
                producto.getControlaVencimiento()
            );
    }


    private Producto crearEntidadDesdeDto(ProductoRequestDTO dto) {

        Producto producto = new Producto();

        actualizarDatosProducto(producto, dto);

        producto.setActivo(true);

        return producto;
    }


    private void actualizarDatosProducto(
            Producto producto,
            ProductoRequestDTO dto) {

        Presentacion presentacion = obtenerPresentacionActivaPorId(dto.idPresentacion());

        Marca marca = obtenerMarcaActivaById(dto.idMarca());

        Categoria categoria = obtenerCategoriaActivaById(dto.idCategoria());

        TarifaIva tarifa = obtenerTarifaActivaById(dto.idTarifaIva());

        producto.setCodigoBarras(
                normalizarCodigoBarras(dto.codigoBarras()));

        producto.setNombre(dto.nombre().trim());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());

        producto.setMarca(marca);
        producto.setCategoria(categoria);
        producto.setPresentacion(presentacion);
        producto.setTarifaIva(tarifa);

        producto.setControlaVencimiento(dto.controlaVencimiento());
    }


    private Producto obtenerProductoPorId(Long id) {

        return productoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RECURSO,
                        id));
    }


    private Presentacion obtenerPresentacionActivaPorId(Long id) {
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("presentacion", id));

        if (!presentacion.getActivo()) {
            throw new BusinessRuleException(
                    "La presentacion seleccionada se encuentra inactiva");
        }

        return presentacion;

    }


    private Marca obtenerMarcaActivaById(Long id) {

        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca", id));

        if (!marca.getActivo()) {
            throw new BusinessRuleException(
                    "La marca seleccionada se encuentra inactiva");
        }

        return marca;
    }


    private Categoria obtenerCategoriaActivaById(Long id) {

        return categoriaRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría",
                        id));
    }


    public ProductoResponseDTO obtenerProducto(Long id) {

        return mapResponse(
                obtenerProductoPorId(id)
        );
    }


    private TarifaIva obtenerTarifaActivaById(Long id) {

        TarifaIva tarifaIva = tarifaIvaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa IVA", id));

        if (!tarifaIva.getActivo()) {
            throw new BusinessRuleException(
                    "La Tarifa IVA seleccionada se encuentra inactiva");
        }

        return tarifaIva;
    }


    private void validarCodigoBarrasCreacion(String codigoBarras) {

        String codigo = normalizarCodigoBarras(codigoBarras);

        if (codigo == null) {
            return;
        }

        if (productoRepository.existsByCodigoBarras(codigo)) {
            throw new DuplicateResourceException(
                    "Código de barras",
                    codigo);
        }
    }


    private void validarCodigoBarrasActualizacion(
            String codigoBarras,
            Long id) {

        String codigo = normalizarCodigoBarras(codigoBarras);

        if (codigo == null) {
            return;
        }

        if (productoRepository
                .existsByCodigoBarrasAndIdNot(codigo, id)) {

            throw new DuplicateResourceException(
                    "Código de barras",
                    codigo);
        }
    }


    private String normalizarCodigoBarras(String codigoBarras) {

        if (codigoBarras == null || codigoBarras.isBlank()) {
            return null;
        }

        return codigoBarras.trim();
    }

}