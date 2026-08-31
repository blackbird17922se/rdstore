package com.dsd.rdstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsd.rdstore.model.ExistenciaProducto;

import jakarta.persistence.LockModeType;

public interface ExistenciaProductoRepository
        extends JpaRepository<ExistenciaProducto, Long> {

    /** Todas las existencias */
    List<ExistenciaProducto> findAllByOrderByFechaIngresoDesc();

    /** Todas las existencias de un producto */
    List<ExistenciaProducto> findByProductoIdOrderByFechaIngresoAsc(Long idProducto);

    /** Existencias que todavía tienen unidades */
    List<ExistenciaProducto> findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(
            Long idProducto,
            Long cantidad);

    /** Próximos vencimientos */
    List<ExistenciaProducto> findByFechaVencimientoBetweenAndCantidadGreaterThanOrderByFechaVencimientoAsc(
            LocalDate fechaInicial,
            LocalDate fechaFinal,
            Long cantidad);

    @Query("""
                SELECT COALESCE(SUM(e.cantidad), 0)
                FROM ExistenciaProducto e
                WHERE e.producto.id = :idProducto
            """)
    Long obtenerStockTotalPorProducto(Long idProducto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT e
                FROM ExistenciaProducto e
                WHERE e.producto.id = :idProducto
                AND e.cantidad > 0
                AND e.fechaVencimiento IS NOT NULL
                AND e.fechaVencimiento >= CURRENT_DATE
                ORDER BY e.fechaVencimiento ASC,
                        e.fechaIngreso ASC
            """)
    List<ExistenciaProducto> buscarDisponiblesFEFO(
            @Param("idProducto") Long idProducto);

    /** Repository para FIFO: Para productos que no vencen */
    @Query("""
                SELECT e
                FROM ExistenciaProducto e
                WHERE e.producto.id = :idProducto
                  AND e.cantidad > 0
                ORDER BY e.fechaIngreso ASC
            """)
    List<ExistenciaProducto> buscarDisponiblesFIFO(
            @Param("idProducto") Long idProducto);
}