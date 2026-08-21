package com.dsd.rdstore.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

// @Entity
// @Data
// @Table(name = "producto")
public class Producto {

    private Long id;
    private String codigo_barras;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean iva;
    private Long id_marca;
    private Long id_tipo;
    private Long id_presentacion;
    private Long stock;

}
