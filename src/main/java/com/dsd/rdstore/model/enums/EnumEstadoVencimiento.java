package com.dsd.rdstore.model.enums;

/** Estado calculado a partir de los días que faltan hasta el vencimiento. */
public enum EnumEstadoVencimiento {
    VENCIDO,
    CRITICO,
    PROXIMO,
    SEGUIMIENTO,
    SIN_ALERTA;

    public static EnumEstadoVencimiento desdeDias(long diasRestantes) {
        if (diasRestantes < 0) return VENCIDO;
        if (diasRestantes <= 7) return CRITICO;
        if (diasRestantes <= 30) return PROXIMO;
        if (diasRestantes <= 60) return SEGUIMIENTO;
        return SIN_ALERTA;
    }
}
