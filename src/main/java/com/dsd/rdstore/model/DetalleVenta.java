package com.dsd.rdstore.model;

import java.math.BigDecimal;

import com.dsd.rdstore.model.enums.EnumTipoIva;

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
@Table(name = "detalle_venta")
@Data
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long cantidad;

    /** Unidades realmente descontadas de las existencias al registrar esta venta. */
    // Null en ventas históricas anteriores a esta funcionalidad: dato no reconstruido.
    @Column(name = "cantidad_descontada")
    private Long cantidadDescontada;

    /** Unidades vendidas sin cobertura en las existencias registradas en DStore. */
    // No implica por sí mismo que el producto esté pendiente de entrega.
    @Column(name = "cantidad_sin_stock")
    private Long cantidadSinStock;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "tipo_iva", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumTipoIva tipoIva;

    @Column(name = "porcentaje_iva", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeIva;

    private BigDecimal subtotal;

    private BigDecimal total;

    @Column(name = "valor_iva", precision = 38, scale = 2, nullable = false)
    private BigDecimal valorIva;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
}


