package com.sportreserve.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IngresoRequest {

    @NotNull(message = "El id de la persona es obligatorio")
    @Positive(message = "El id de la persona debe ser positivo")
    private Long personaId;

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }
}
