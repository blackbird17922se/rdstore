package com.dsd.rdstore.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.enums.TipoIva;

public interface TarifaIvaRepository
        extends JpaRepository<TarifaIva, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(
        String nombre,
        Long id
    );

    boolean existsByTipoAndPorcentaje(
        TipoIva tipo,
        BigDecimal porcentaje
    );

    boolean existsByTipoAndPorcentajeAndIdNot(
        TipoIva tipo,
        BigDecimal porcentaje,
        Long id
    );

    List<TarifaIva> findByActivoTrueOrderByPorcentajeAsc();
}