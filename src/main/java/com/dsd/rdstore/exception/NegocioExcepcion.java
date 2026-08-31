package com.dsd.rdstore.exception;

public class NegocioExcepcion extends RuntimeException {

    public NegocioExcepcion(String mensaje) {
        super(mensaje);
    }

    public NegocioExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}