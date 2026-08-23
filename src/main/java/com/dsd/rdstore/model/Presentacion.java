package com.dsd.rdstore.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "presentacion")
@Data
public class Presentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo;
}