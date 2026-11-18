package com.dsd.rdstore.common.util;

import java.util.Set;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsd.rdstore.dto.common.PageResponseDTO;

public class SpecificationPageUtils {

    private SpecificationPageUtils() {}

    /** 
     * E: entidad JPA, por ejemplo Categoria.
     * D: DTO de respuesta, por ejemplo CategoriaResponseDTO.
     * Function<E, D> permite que cada servicio proporcione 
     *  su propio mapResponse().
    */
    public static <E, D> PageResponseDTO<D> buscar(
            JpaSpecificationExecutor<E> repository,
            Specification<E> specification,
            Function<E, D> mapper,
            int page,
            int size,
            String ordenarPor,
            String direccion,
            Set<String> camposOrden) {

        Pageable pageable = PaginationUtils.crear(
                page, size, ordenarPor, direccion, camposOrden);

        Page<D> resultados = repository
                .findAll(specification, pageable)
                .map(mapper);

        return PageResponseDTO.from(resultados);
    }
}
