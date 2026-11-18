package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsd.rdstore.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>
    , JpaSpecificationExecutor<Categoria>{

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(
        String nombre, 
        Long id
    );

    List<Categoria> findByActivoTrueOrderByNombreAsc();
}
