package com.sportreserve.dto.response;

import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.enums.TipoEscenario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaResponse {

    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;

    private Long escenarioId;
    private String escenarioNombre;
    private TipoEscenario tipoEscenario;

    private Long responsableId;
    private String responsableNombre;
    private RolParticipacion rolResponsable;

    private List<ParticipanteResponse> participantes;
    private PagoResponse pago;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Long getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Long escenarioId) {
        this.escenarioId = escenarioId;
    }

    public String getEscenarioNombre() {
        return escenarioNombre;
    }

    public void setEscenarioNombre(String escenarioNombre) {
        this.escenarioNombre = escenarioNombre;
    }

    public TipoEscenario getTipoEscenario() {
        return tipoEscenario;
    }

    public void setTipoEscenario(TipoEscenario tipoEscenario) {
        this.tipoEscenario = tipoEscenario;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public RolParticipacion getRolResponsable() {
        return rolResponsable;
    }

    public void setRolResponsable(RolParticipacion rolResponsable) {
        this.rolResponsable = rolResponsable;
    }

    public List<ParticipanteResponse> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteResponse> participantes) {
        this.participantes = participantes;
    }

    public PagoResponse getPago() {
        return pago;
    }

    public void setPago(PagoResponse pago) {
        this.pago = pago;
    }
}
