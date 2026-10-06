package com.sportreserve.exception;

public class IdentificacionDuplicadaException extends BusinessException {

    public IdentificacionDuplicadaException(String identificacion) {
        super(String.format("Ya existe una persona registrada con la identificacion: %s", identificacion));
    }
}
