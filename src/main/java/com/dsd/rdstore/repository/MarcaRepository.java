package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsd.rdstore.model.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long>, 
    JpaSpecificationExecutor<Marca>{

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Marca> findByActivoTrueOrderByNombreAsc();


}
