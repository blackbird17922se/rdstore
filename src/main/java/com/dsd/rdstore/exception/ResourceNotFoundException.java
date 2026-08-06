package com.dsd.rdstore.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Object identificador) {
        super(recurso + " no encontrado con identificador: " + identificador);
    }
}