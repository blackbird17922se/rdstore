package com.dsd.rdstore.service;

import com.dsd.rdstore.repository.DetalleVentaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.dsd.rdstore.common.util.SpecificationPageUtils;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.venta.DetalleVentaRequestDTO;
import com.dsd.rdstore.dto.venta.VentaRequestDTO;
import com.dsd.rdstore.dto.venta.VentaResponseDTO;
import com.dsd.rdstore.exception.NegocioExcepcion;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Cliente;
import com.dsd.rdstore.model.DetalleVenta;
import com.dsd.rdstore.model.Producto;
import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.model.enums.EnumEstadoVenta;
import com.dsd.rdstore.repository.ClienteRepository;
import com.dsd.rdstore.repository.ProductoRepository;
import com.dsd.rdstore.repository.UsuarioRepository;
import com.dsd.rdstore.repository.VentaRepository;
import com.dsd.rdstore.specification.VentaSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    private final SalidaInventarioService salidaInventarioService;

    // Ordenamiento controlado: nunca interpolar propiedades arbitrarias del cliente.
    private static final Set<String> CAMPOS_ORDEN = Set.of(
            "id", "fecha", "total", "subtotal", "ivaTotal", "estado");


    public List<VentaResponseDTO> listarVentas() {
        return ventaRepository
            .findAllByOrderByFechaDesc()
            .stream()
            .map(this::mapearVentaResponse)
            .toList();
    }

    /**
     * Consulta paginada del historial comercial, sin cargar todos los detalles.
     * El detalle completo se consulta por ID al abrir la venta seleccionada.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponseDTO<VentaResponseDTO> listarVentasPaginadas(
            String texto,
            EnumEstadoVenta estado,
            Long idCliente,
            Long idVendedor,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal totalMin,
            BigDecimal totalMax,
            int page,
            int size,
            String ordenarPor,
            String direccion) {

        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede superar la fecha final");
        }
        if (totalMin != null && totalMin.signum() < 0 || totalMax != null && totalMax.signum() < 0) {
            throw new IllegalArgumentException("Los totales no pueden ser negativos");
        }
        if (totalMin != null && totalMax != null && totalMin.compareTo(totalMax) > 0) {
            throw new IllegalArgumentException("El total mínimo no puede superar el máximo");
        }

        return SpecificationPageUtils.buscar(
                ventaRepository,
                VentaSpecification.filtrar(
                        texto, estado, idCliente, idVendedor,
                        fechaDesde, fechaHasta, totalMin, totalMax),
                this::mapearVentaResponse,
                page, size, ordenarPor, direccion, CAMPOS_ORDEN);
    }


    public Venta actualizar(Long id, Venta datosVenta) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrado"));

        existente.setFecha(datosVenta.getFecha());
        existente.setTotal(datosVenta.getTotal());
        existente.setCliente(datosVenta.getCliente());
        existente.setSubtotal(datosVenta.getSubtotal());
        existente.setIvaTotal(datosVenta.getIvaTotal());
        existente.setIdVendedor(datosVenta.getIdVendedor());
        return ventaRepository.save(existente);
    }

    public Venta cambiarEstadoVenta(Long id, EnumEstadoVenta nuevoEstado) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrado"));

        existente.setEstado(nuevoEstado);
        return ventaRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrado");
        }
        ventaRepository.deleteById(id);
    }


    @Transactional
    public Venta registrarVenta(VentaRequestDTO request, Authentication authentication) {

        String nombreUsuario = authentication.getName();

        // 2. Obtener vendedor autenticado
        Usuario vendedor = validarUsuarioActivo(nombreUsuario);

        // 3. Obtener cliente si fue enviado (opcional)
        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = obtenerClienteRepository(request.idCliente());
        }

        /* Crear cabecera de venta */
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EnumEstadoVenta.CONFIRMADA);
        venta.setCliente(cliente);
        venta.setIdVendedor(vendedor);
        venta.setObservacion(request.observacion());

        /* guardamos la venta antes de procesar los detalles. */
        venta.setSubtotal(BigDecimal.ZERO);
        venta.setIvaTotal(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);

        venta = ventaRepository.save(venta);

        /* 4. Acumuladores */
        BigDecimal subtotalVenta = BigDecimal.ZERO;
        BigDecimal ivaVenta = BigDecimal.ZERO;
        BigDecimal totalVenta = BigDecimal.ZERO;

        /* 5. Procesar detalles */
        for (DetalleVentaRequestDTO reqDetalle : request.detalles()) {

            Producto producto = obtenerProductoPorId(reqDetalle.idProducto());

            TarifaIva tarifaIva = producto.getTarifaIva();

            DetalleVenta detalle = crearDetalleVenta(reqDetalle, venta, producto, tarifaIva);

            /* descontar existencia. FEFO / FIFO y movimiento */
            // El descuento físico se limita a las unidades realmente disponibles.
            // La venta comercial puede incluir unidades sin cobertura de inventario.
            SalidaInventarioService.ResultadoDescuentoInventario resultado =
                    salidaInventarioService.descontarPorVenta(
                            producto, reqDetalle.cantidad(), venta.getId());

            // Persistir ambas cantidades por detalle para futuras alertas y auditoría.
            detalle.setCantidadDescontada(resultado.cantidadDescontada());
            detalle.setCantidadSinStock(resultado.cantidadSinStock());
            detalleVentaRepository.save(detalle);

            /* Acumular valores ya calculados */
            subtotalVenta = subtotalVenta.add(detalle.getSubtotal());
            ivaVenta = ivaVenta.add(detalle.getValorIva());
            totalVenta = totalVenta.add(detalle.getTotal());
        }

        /* 6. Totales definitivos */
        venta.setSubtotal(subtotalVenta);
        venta.setIvaTotal(ivaVenta);
        venta.setTotal(totalVenta);

        return ventaRepository.save(venta);
    }


    private DetalleVenta crearDetalleVenta(DetalleVentaRequestDTO dto,
          Venta venta, Producto producto, TarifaIva tarifaIva){

            /* Calculo del iva y precio*/
            BigDecimal precioUnitario  = producto.getPrecio(); // ya contiene el iva
            BigDecimal porcentajeIva = producto.getTarifaIva().getPorcentaje();
            BigDecimal cantidad = BigDecimal.valueOf(dto.cantidad());
            BigDecimal totalDetalle = precioUnitario.multiply(cantidad);

            BigDecimal subtotal;
            BigDecimal valorIva;

            // si aplica el iva
            if (porcentajeIva.compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal porcentajeDecimal = porcentajeIva.divide(
                    BigDecimal.valueOf(100),
                    4,
                    RoundingMode.HALF_UP
                );

                BigDecimal factorIva = BigDecimal.ONE.add(porcentajeDecimal);

                subtotal = totalDetalle.divide(
                    factorIva,
                    2,
                    RoundingMode.HALF_UP
                );

                valorIva = totalDetalle.subtract(subtotal);

            } else {

                subtotal = totalDetalle;
                valorIva = BigDecimal.ZERO;
            }


            DetalleVenta detalle = new DetalleVenta();

            detalle.setVenta(venta);
            detalle.setProducto(producto);

            detalle.setCantidad(dto.cantidad());

            detalle.setPrecioUnitario(precioUnitario);
            detalle.setTipoIva(tarifaIva.getTipo());
            detalle.setPorcentajeIva(porcentajeIva);
            detalle.setSubtotal(subtotal);
            detalle.setValorIva(valorIva);
            detalle.setTotal(totalDetalle);

            return detalle;
    }


    @Transactional
    public Venta confirmarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() != EnumEstadoVenta.BORRADOR) {
            throw new IllegalStateException("Solo se puede confirmar una venta en BORRADOR");
        }

        // validar stock
        // descontar inventario

        venta.setEstado(EnumEstadoVenta.CONFIRMADA);
        return venta;
    }

    @Transactional
    public void anularVenta(
            Long idVenta,
            String motivo) {

        // Solo una solicitud puede anular esta venta a la vez.
        // El bloqueo dura hasta confirmar o revertir la transacción.
        Venta venta = ventaRepository.obtenerPorIdConBloqueo(idVenta)
                .orElseThrow(() ->
                        new NegocioExcepcion(
                                "Venta no encontrada"
                        )
                );

        if (motivo == null || motivo.isBlank()) {
            throw new NegocioExcepcion("El motivo de anulación es obligatorio");
        }
        String motivoNormalizado = motivo.trim();
        // La columna motivo_anulacion no define longitud; JPA/MySQL suele usar VARCHAR(255).
        if (motivoNormalizado.length() > 255) {
            throw new NegocioExcepcion("El motivo de anulación no puede superar 255 caracteres");
        }

        if (venta.getEstado() == EnumEstadoVenta.ANULADA) {
            throw new NegocioExcepcion(
                    "La venta ya se encuentra anulada"
            );
        }

        if (venta.getEstado() != EnumEstadoVenta.CONFIRMADA) {
            throw new NegocioExcepcion("Solo se pueden anular ventas confirmadas");
        }

        /*
        * Restaurar exactamente las existencias
        * afectadas por la venta.
        */
        salidaInventarioService.revertirVenta(idVenta);

        venta.setEstado(
                EnumEstadoVenta.ANULADA
        );

        venta.setFechaAnulacion(
                LocalDateTime.now()
        );

        venta.setMotivoAnulacion(motivoNormalizado);

        ventaRepository.save(venta);
    }


    private Producto obtenerProductoPorId(Long id) {

        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto",id));
    }

    private Cliente obtenerClienteRepository(Long id){

        return clienteRepository
            .findById(id)
                .orElseThrow(()-> 
                    new ResourceNotFoundException("Cliente", id)
        );
    }

    private Usuario validarUsuarioActivo(String nombreUsuario){

        return usuarioRepository.findByNombreUsuarioAndActivoTrue(nombreUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", nombreUsuario)); 

    }


    private VentaResponseDTO mapearVentaResponse(Venta venta) {

        Long idCliente = null;
        String nombreCliente = null;

        if (venta.getCliente() != null) {
            idCliente = venta.getCliente().getId();
            nombreCliente = venta.getCliente().getNombresApellidos();
        }

        return new VentaResponseDTO(
            venta.getId(),
            venta.getFecha(),
            idCliente,
            nombreCliente,
            venta.getSubtotal(),
            venta.getIvaTotal(),
            venta.getTotal(),
            venta.getIdVendedor().getId(),
            venta.getIdVendedor().getNombreUsuario(),
            venta.getEstado(),
            venta.getFechaAnulacion(),
            venta.getMotivoAnulacion()
        );
    }


    
}
