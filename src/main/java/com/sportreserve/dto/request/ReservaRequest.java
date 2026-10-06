package com.sportreserve.dto.request;

import com.sportreserve.enums.RolParticipacion;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaRequest {

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser actual o futura")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "El id del escenario es obligatorio")
    @Positive(message = "El id del escenario debe ser positivo")
    private Long escenarioId;

    @NotNull(message = "El id del responsable es obligatorio")
    @Positive(message = "El id del responsable debe ser positivo")
    private Long responsableId;

    @NotNull(message = "El rol del responsable es obligatorio")
    private RolParticipacion rolResponsable;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Long getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Long escenarioId) {
        this.escenarioId = escenarioId;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public RolParticipacion getRolResponsable() {
        return rolResponsable;
    }

    public void setRolResponsable(RolParticipacion rolResponsable) {
        this.rolResponsable = rolResponsable;
    }
}
