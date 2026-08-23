package com.dsd.rdstore.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }

    public DuplicateResourceException(String recurso, Object identificador) {
        super("Ya existe " + recurso + " con el nombre: " + identificador);
    }
}
