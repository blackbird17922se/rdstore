package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.venta.DetalleVentaDTO;
import com.dsd.rdstore.dto.venta.VentaDetalleDTO;
import com.dsd.rdstore.model.Venta;
import com.dsd.rdstore.repository.DetalleVentaRepository;
import com.dsd.rdstore.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository DetalleVentaRepository;
    private final VentaRepository ventaRepository;


//     public VentaDetalleDTO obtenerDetalleVenta(Integer idVenta) {

//         Venta venta = ventaRepository.findById(idVenta)
//                 .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

//         List<DetalleVentaDTO> items = DetalleVentaRepository
//                 .findByVentaId(idVenta)
//                 .stream()
//                 .map(d -> new DetalleVentaDTO(
//                         d.getId(),
//                         venta.getId(),
//                         d.getIdProducto().getNombre(),
//                         d.getCantidad(),
//                         d.getPrecioUnitario(),
//                         d.getSubtotal()))
//                 .toList();

//         return new VentaDetalleDTO(
//                 venta.getId(),
//                 venta.getFecha(),
//                 venta.getCliente(),
//                 venta.getTotal(),
//                 venta.getIdVendedor().getNombre() + " " + venta.getIdVendedor().getApellido(),
//                 venta.getEstado().name(),
//                 venta.getFechaAnulacion(),
//                 venta.getMotivoAnulacion(),
//                 items);
//     }
}
