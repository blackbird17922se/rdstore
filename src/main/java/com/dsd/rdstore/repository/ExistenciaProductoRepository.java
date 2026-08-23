package com.dsd.rdstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
        SELECT COALESCE(SUM(e.cantidad), 0)
        FROM ExistenciaProducto e
        WHERE e.producto.id = :idProducto
    """)
    Long obtenerStockTotalPorProducto(Long idProducto);
}