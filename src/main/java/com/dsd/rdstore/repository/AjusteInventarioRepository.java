package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.AjusteInventario;

public interface AjusteInventarioRepository
        extends JpaRepository<AjusteInventario, Long> {

    List<AjusteInventario>
            findByExistenciaIdOrderByFechaAjusteDesc(Long idExistencia);

    List<AjusteInventario>
            findByExistenciaProductoIdOrderByFechaAjusteDesc(Long idProducto);

    List<AjusteInventario>
            findAllByOrderByFechaAjusteDesc();
}