package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.MovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;

public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario>
            findByExistenciaIdOrderByFechaMovimientoDesc(Long idExistencia);

    List<MovimientoInventario>
            findByExistenciaProductoIdOrderByFechaMovimientoDesc(
                    Long idProducto);

    List<MovimientoInventario>
            findByTipoOrigenAndIdOrigenOrderByIdAsc(
                    EnumTipoOrigenInventario tipoOrigen,
                    Long idOrigen);
}