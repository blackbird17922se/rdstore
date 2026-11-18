package com.dsd.rdstore.common.pagination;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.dsd.rdstore.common.util.PaginationUtils;

class PaginationUtilsTest {

    private static final Set<String> CAMPOS = Set.of("id", "nombre", "precio");

    @Test
    void creaPaginacionConOrdenYDesempate() {
        Pageable pageable = PaginationUtils.crear(2, 25, "nombre", "desc", CAMPOS);

        assertEquals(2, pageable.getPageNumber());
        assertEquals(25, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("nombre").getDirection());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("id").getDirection());
    }

    @Test
    void rechazaTamanoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationUtils.crear(0, 101, "nombre", "asc", CAMPOS));
    }

    @Test
    void rechazaCampoNoAutorizado() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationUtils.crear(0, 10, "campoDesconocido", "asc", CAMPOS));
    }

    @Test
    void rechazaDireccionInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationUtils.crear(0, 10, "nombre", "invalida", CAMPOS));
    }
}
