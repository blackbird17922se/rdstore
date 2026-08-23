package com.dsd.rdstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);
}
