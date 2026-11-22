package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsd.rdstore.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente>{

    boolean existsByNombresApellidosIgnoreCase(String nombre);

    List<Cliente> findByActivoTrueOrderByNombresApellidosAsc();

    boolean existsByNombresApellidosIgnoreCaseAndIdNot(String nombre, Long id);
}