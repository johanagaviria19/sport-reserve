package com.sportreserve.dto.request;

import com.sportreserve.enums.RolParticipacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ParticipanteRequest {

    @NotNull(message = "El id de la persona es obligatorio")
    @Positive(message = "El id de la persona debe ser positivo")
    private Long personaId;

    @NotNull(message = "El rol es obligatorio")
    private RolParticipacion rol;

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public RolParticipacion getRol() {
        return rol;
    }

    public void setRol(RolParticipacion rol) {
        this.rol = rol;
    }
}
