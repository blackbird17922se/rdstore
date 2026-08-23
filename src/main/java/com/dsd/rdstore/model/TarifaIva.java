package com.dsd.rdstore.model;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.TipoIva;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "tarifa_iva",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_tarifa_iva_tipo_porcentaje",
            columnNames = {"tipo", "porcentaje"}
        )
    }
)
public class TarifaIva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        unique = true,
        length = 80
    )
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 20
    )
    private TipoIva tipo;

    @Column(
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal porcentaje;

    @Column(nullable = false)
    private Boolean activo;
}