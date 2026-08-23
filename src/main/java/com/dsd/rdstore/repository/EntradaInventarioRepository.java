package com.dsd.rdstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.EntradaInventario;

public interface EntradaInventarioRepository
        extends JpaRepository<EntradaInventario, Long> {
}