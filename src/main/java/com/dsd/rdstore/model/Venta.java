package com.dsd.rdstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dsd.rdstore.model.enums.EnumEstadoVenta;

import jakarta.persistence.CascadeType;
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

@Entity
@Table(name = "venta")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumEstadoVenta estado;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @Column(name = "iva_total")
    private BigDecimal ivaTotal;

    private String observacion;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario idVendedor;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente cliente;

}
