package com.sportreserve.observer;

import com.sportreserve.enums.EstadoReserva;

import java.time.LocalDateTime;

public class ReservaEvent {

    private final Long reservaId;
    private final EstadoReserva estado;
    private final LocalDateTime fechaHora;

    public ReservaEvent(Long reservaId, EstadoReserva estado) {
        this(reservaId, estado, LocalDateTime.now());
    }

    public ReservaEvent(Long reservaId, EstadoReserva estado, LocalDateTime fechaHora) {
        this.reservaId = reservaId;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
