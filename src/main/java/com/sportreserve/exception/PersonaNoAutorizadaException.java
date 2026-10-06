package com.sportreserve.exception;

import com.sportreserve.enums.RolParticipacion;

public class PersonaNoAutorizadaException extends BusinessException {

    public PersonaNoAutorizadaException(String message) {
        super(message);
    }

    public PersonaNoAutorizadaException(Long personaId, RolParticipacion rolActual, RolParticipacion rolRequerido) {
        super(String.format("La persona %d no esta autorizada. Rol actual: %s, Rol requerido: %s",
                personaId, rolActual, rolRequerido));
    }
}
