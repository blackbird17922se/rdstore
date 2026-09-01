package com.dsd.rdstore.model;

import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.EnumTipoMovimientoInventario;
import com.dsd.rdstore.model.enums.EnumTipoOrigenInventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Representa un cambio en las existencias de inventario.
 *
 * Cada movimiento registra la cantidad que ingresó o salió de una
 * existencia específica, el tipo de operación que originó el cambio
 * y la referencia a la operación de negocio correspondiente.
 *
 * Los movimientos constituyen el historial del inventario y no deben
 * modificarse ni eliminarse una vez registrados.
 */
@Entity
@Data
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_existencia",
        nullable = false
    )
    private ExistenciaProducto existencia;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 30
    )
    private EnumTipoMovimientoInventario tipo;

    @Column(nullable = false)
    private Long cantidad;

    @Column(
        name = "fecha_movimiento",
        nullable = false
    )
    private LocalDateTime fechaMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "tipo_origen",
        length = 30
    )
    private EnumTipoOrigenInventario tipoOrigen;

    @Column(name = "id_origen")
    private Long idOrigen;

    @Column(length = 500)
    private String observacion;
}