package com.sportreserve.dto.response;

import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.RolParticipacion;

import java.time.LocalDateTime;

public class ParticipanteResponse {

    private Long personaId;
    private String nombreCompleto;
    private String identificacion;
    private RolParticipacion rol;
    private EstadoIngreso estadoIngreso;
    private LocalDateTime fechaHoraIngreso;

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public RolParticipacion getRol() {
        return rol;
    }

    public void setRol(RolParticipacion rol) {
        this.rol = rol;
    }

    public EstadoIngreso getEstadoIngreso() {
        return estadoIngreso;
    }

    public void setEstadoIngreso(EstadoIngreso estadoIngreso) {
        this.estadoIngreso = estadoIngreso;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }
}
