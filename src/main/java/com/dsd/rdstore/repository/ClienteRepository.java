package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    boolean existsByNombresApellidosIgnoreCase(String nombre);

    List<Cliente> findByActivoTrueOrderByNombresApellidosAsc();

    boolean existsByNombresApellidosIgnoreCaseAndIdNot(String nombre, Long id);
}