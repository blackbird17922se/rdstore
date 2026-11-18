package com.dsd.rdstore.common.util;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Crea un Pageable validado. Los campos permitidos los define cada módulo;
 * nunca se admite un nombre de propiedad arbitrario desde el cliente.
 *
 * Lanza IllegalArgumentException si los parámetros no son válidos.
 * El controlador o el manejador global debe convertirlo a HTTP 400.
 */
public final class PaginationUtils {

    public static final int MAX_PAGE_SIZE = 100;

    private PaginationUtils() {
        // Clase de utilidad: no instanciable.
    }

    public static Pageable crear(
            int page,
            int size,
            String campoOrden,
            String direccion,
            Set<String> camposPermitidos) {

        if (page < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "El tamaño de página debe estar entre 1 y " + MAX_PAGE_SIZE);
        }

        if (camposPermitidos == null || campoOrden == null
                || !camposPermitidos.contains(campoOrden)) {
            throw new IllegalArgumentException("Campo de ordenamiento no permitido");
        }

        if (!"asc".equalsIgnoreCase(direccion)
                && !"desc".equalsIgnoreCase(direccion)) {
            throw new IllegalArgumentException("La dirección debe ser asc o desc");
        }

        Sort.Direction sentido = Sort.Direction.fromString(direccion);
        Sort orden = Sort.by(new Sort.Order(sentido, campoOrden));

        // Desempate estable si el modelo expone una propiedad 'id' autorizada.
        if (!"id".equals(campoOrden) && camposPermitidos.contains("id")) {
            orden = orden.and(Sort.by(Sort.Direction.ASC, "id"));
        }

        return PageRequest.of(page, size, orden);
    }
}
