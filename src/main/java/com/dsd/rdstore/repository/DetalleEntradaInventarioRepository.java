package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.DetalleEntradaInventario;

public interface DetalleEntradaInventarioRepository
        extends JpaRepository<DetalleEntradaInventario, Long> {

    List<DetalleEntradaInventario> findByEntradaIdOrderByIdAsc(Long idEntrada);
}