package com.sportreserve.exception;

public class RecursoNoEncontradoException extends BusinessException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(String.format("%s no encontrado(a) con id: %d", recurso, id));
    }
}
