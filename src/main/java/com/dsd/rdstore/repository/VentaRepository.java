package com.dsd.rdstore.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsd.rdstore.model.Venta;


public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    List<Venta> findAllByOrderByFechaDesc();

    /**
     * Bloquea la cabecera antes de comprobar su estado y anularla.
     * Impide que dos solicitudes concurrentes devuelvan dos veces el inventario.
     * Debe utilizarse dentro de una transacción.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Venta v WHERE v.id = :id")
    Optional<Venta> obtenerPorIdConBloqueo(@Param("id") Long id);
    
}
