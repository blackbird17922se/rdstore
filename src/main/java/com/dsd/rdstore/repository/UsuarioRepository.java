package com.dsd.rdstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
