package com.dsd.rdstore.specification;

import org.springframework.data.jpa.domain.Specification;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.specification.common.CommonSpecifications;

public class CategoriaSpecification {

    private CategoriaSpecification() {}

    public static Specification<Categoria> filtrar(String nombre, Boolean activo) {

        return CommonSpecifications.<Categoria>nombreContiene(nombre)
            .and(CommonSpecifications.<Categoria>tieneEstado(activo));
    }
    
}
