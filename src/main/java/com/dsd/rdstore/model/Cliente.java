package com.dsd.rdstore.model;

import java.time.LocalDate;

import com.dsd.rdstore.model.enums.EnumTipoDocumento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** @author Mauricio Alarcon */
@Entity
@Data
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20)
    private EnumTipoDocumento tipoDocumento;

    @Column(name = "numero_documento", unique = true, length = 30)
    private String numeroDocumento ;

    @Column(name = "nombres_apellidos", unique = true, nullable = false, length = 100)
    private String nombresApellidos;

    @Column(length = 30)
    private String telefono;

    @Column(length = 150)
    private String correo;

    @Column(length = 250)
    private String direccion;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private Boolean activo;

}
