package com.dsd.rdstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Presentacion;

public interface PresentacionRepository
        extends JpaRepository<Presentacion, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(
        String nombre,
        Long id
    );

    List<Presentacion> findByActivoTrueOrderByNombreAsc();

    Optional<Presentacion> findByNombreIgnoreCase(String nombre);
}