package com.dsd.rdstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /* Optional es una forma de Java de decir:
    * “Este valor puede existir… o puede que no.” 
    Si Optional tiene Usuario
    → dámelo
    Si Optional está vacío
    → lanza la excepción */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    //boolean existsByNombreUsuarioIgnoreCaseAndIdNot(String nombreUsuario, Long id);
    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);
}
