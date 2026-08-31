package com.dsd.rdstore.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.DetalleVenta;


public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    // filtrar por el id_venta
    List<DetalleVenta> findByVentaId(Long idVenta);
}
