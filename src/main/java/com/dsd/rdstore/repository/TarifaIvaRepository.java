package com.dsd.rdstore.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.enums.EnumTipoIva;

public interface TarifaIvaRepository
        extends JpaRepository<TarifaIva, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(
        String nombre,
        Long id
    );

    boolean existsByTipoAndPorcentaje(
        EnumTipoIva tipo,
        BigDecimal porcentaje
    );

    boolean existsByTipoAndPorcentajeAndIdNot(
        EnumTipoIva tipo,
        BigDecimal porcentaje,
        Long id
    );

    List<TarifaIva> findByActivoTrueOrderByPorcentajeAsc();
}