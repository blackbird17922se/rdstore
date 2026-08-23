package com.dsd.rdstore.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "entrada_inventario")
public class EntradaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** fechaEntrada sería cuándo llegó físicamente la mercancía al negocio. */
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    /** echaRegistro sería cuándo el usuario registró esa entrada dentro de DStore. */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /** número del documento con el que llegó esa mercancía, si existe. */
    @Column(name = "numero_documento", length = 100)
    private String numeroDocumento;

    @Column(length = 500)
    private String observacion;
}