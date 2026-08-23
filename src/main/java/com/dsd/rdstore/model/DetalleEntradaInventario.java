package com.dsd.rdstore.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/** Conserva los datos que entraron historicamente, ExistenciaProducto
 * representa la existencia actual del item
 */
@Entity
@Data
@Table(name = "detalle_entrada_inventario")
public class DetalleEntradaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_entrada",
        nullable = false
    )
    private EntradaInventario entrada;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_producto",
        nullable = false
    )
    private Producto producto;

    @Column(nullable = false)
    private Long cantidad;

    @Column(
        name = "numero_lote",
        length = 100
    )
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}