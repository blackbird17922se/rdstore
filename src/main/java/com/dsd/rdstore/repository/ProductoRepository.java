package com.dsd.rdstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrueOrderByNombreAsc();

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsByCodigoBarrasAndIdNot(
            String codigoBarras,
            Long id);
            
    boolean existsByCategoriaId(Long idCategoria);

}
