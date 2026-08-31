package com.dsd.rdstore.model;

import java.time.LocalDateTime;

import com.dsd.rdstore.model.enums.EnumTipoAjusteInventario;

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
 * Representa una corrección manual realizada sobre una existencia
 * específica del inventario.
 *
 * Cada ajuste conserva el tipo de corrección, la cantidad afectada,
 * su motivo y el usuario responsable de realizarla.
 *
 * Los ajustes generan movimientos de inventario y forman parte
 * del historial, por lo que no deben modificarse ni eliminarse
 * después de ser registrados.
 */
@Entity
@Data
@Table(name = "ajuste_inventario")
public class AjusteInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_existencia",
        nullable = false
    )
    private ExistenciaProducto existencia;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_usuario",
        nullable = false
    )
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 20
    )
    private EnumTipoAjusteInventario tipo;

    @Column(nullable = false)
    private Long cantidad;

    @Column(
        nullable = false,
        length = 150
    )
    private String motivo;

    @Column(length = 500)
    private String observacion;

    @Column(
        name = "fecha_ajuste",
        nullable = false
    )
    private LocalDateTime fechaAjuste;
}