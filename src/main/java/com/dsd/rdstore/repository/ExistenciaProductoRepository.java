package com.dsd.rdstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.ExistenciaProducto;

public interface ExistenciaProductoRepository
        extends JpaRepository<ExistenciaProducto, Long> {

    /** Todas las existencias de un producto */
    List<ExistenciaProducto>
        findByProductoIdOrderByFechaIngresoAsc(Long idProducto);

    /** Existencias que todavía tienen unidades */
    List<ExistenciaProducto>
        findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(
                Long idProducto,
                Long cantidad
        );

    /** Próximos vencimientos */
    List<ExistenciaProducto>
        findByFechaVencimientoBetweenAndCantidadGreaterThanOrderByFechaVencimientoAsc(
                LocalDate fechaInicial,
                LocalDate fechaFinal,
                Long cantidad
        );
}