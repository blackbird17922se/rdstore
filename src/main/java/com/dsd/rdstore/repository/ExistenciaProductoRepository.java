package com.dsd.rdstore.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsd.rdstore.model.ExistenciaProducto;

import jakarta.persistence.LockModeType;

public interface ExistenciaProductoRepository
        extends JpaRepository<ExistenciaProducto, Long>, JpaSpecificationExecutor<ExistenciaProducto> {

    /**
     * SELECT ... FOR UPDATE (en MySQL/InnoDB) para ajustes manuales.
     * Se debe llamar desde un método @Transactional.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ExistenciaProducto e WHERE e.id = :id")
    Optional<ExistenciaProducto> obtenerPorIdConBloqueo(@Param("id") Long id);

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
    // Igual que FEFO, protege los lotes elegidos ante descuentos concurrentes.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
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