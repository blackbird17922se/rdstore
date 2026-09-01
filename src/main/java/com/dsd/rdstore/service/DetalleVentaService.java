package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.venta.DetalleVentaDTO;
import com.dsd.rdstore.dto.venta.VentaDetalleDTO;
import com.dsd.rdstore.model.DetalleVenta;
import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.repository.DetalleVentaRepository;
import com.dsd.rdstore.repository.VentaRepository;

import lombok.RequiredArgsConstructor;
/** Migrado de DStore v1
 * @author mauro a.
 */
@Service
@RequiredArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;


    public VentaDetalleDTO obtenerDetalleVenta(Long idVenta) {

        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        List<DetalleVenta> detalles =
            detalleVentaRepository.findByVentaId(idVenta);

        return mapearVentaDetalleResponse(venta, detalles);
    }


    private VentaDetalleDTO mapearVentaDetalleResponse(
        Venta venta, List<DetalleVenta> detalles) {

        List<DetalleVentaDTO> items = 
            detalles.stream()
            .map(d -> new DetalleVentaDTO(
                d.getId(),
                venta.getId(),
                d.getProducto().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
            )).toList();

        String nombreCliente = null;

        if (venta.getCliente() != null) {
            nombreCliente = venta.getCliente().getNombresApellidos();
        }

        return new VentaDetalleDTO(
            venta.getId(),
            venta.getFecha(),
            nombreCliente,
            venta.getTotal(),
            venta.getIdVendedor().getNombreUsuario(),
            venta.getEstado(),
            venta.getFechaAnulacion(),
            venta.getMotivoAnulacion(),
            items);
    }
}
