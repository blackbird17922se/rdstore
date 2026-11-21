package com.dsd.rdstore.model.enums;

public enum EnumTipoMovimientoInventario {
    ENTRADA,
    VENTA,
    AJUSTE_ENTRADA,
    AJUSTE_SALIDA,
    /** Devolución física de unidades descontadas por una venta posteriormente anulada. */
    ANULACION_VENTA
}
