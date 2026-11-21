package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsd.rdstore.model.AjusteInventario;

public interface AjusteInventarioRepository
        extends JpaRepository<AjusteInventario, Long>, JpaSpecificationExecutor<AjusteInventario> {

    List<AjusteInventario>
            findByExistenciaIdOrderByFechaAjusteDesc(Long idExistencia);

    List<AjusteInventario>
            findByExistenciaProductoIdOrderByFechaAjusteDesc(Long idProducto);

    List<AjusteInventario>
            findAllByOrderByFechaAjusteDesc();
}
