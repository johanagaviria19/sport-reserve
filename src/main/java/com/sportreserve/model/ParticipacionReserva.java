package com.sportreserve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.RolParticipacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "participaciones_reserva",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_participacion_reserva_persona",
                        columnNames = {"reserva_id", "persona_id"})
        })
public class ParticipacionReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 15)
    private RolParticipacion rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_ingreso", nullable = false, length = 15)
    private EstadoIngreso estadoIngreso;

    @Column(name = "fecha_hora_ingreso")
    private LocalDateTime fechaHoraIngreso;

    public ParticipacionReserva() {
    }

    public ParticipacionReserva(Persona persona, Reserva reserva, RolParticipacion rol) {
        this.persona = persona;
        this.reserva = reserva;
        this.rol = rol;
        this.estadoIngreso = EstadoIngreso.REGISTRADO;
        this.fechaHoraIngreso = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
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

    public void registrarIngreso() {
        this.estadoIngreso = EstadoIngreso.INGRESO;
        this.fechaHoraIngreso = LocalDateTime.now();
    }

    public boolean esJugador() {
        return RolParticipacion.JUGADOR == this.rol;
    }

    public boolean esEspectador() {
        return RolParticipacion.ESPECTADOR == this.rol;
    }

    public boolean haIngresado() {
        return EstadoIngreso.INGRESO == this.estadoIngreso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipacionReserva that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : System.identityHashCode(this));
    }

    @Override
    public String toString() {
        return "ParticipacionReserva{id=" + id + ", persona=" + (persona != null ? persona.getId() : null) +
                ", reserva=" + (reserva != null ? reserva.getId() : null) +
                ", rol=" + rol + ", estadoIngreso=" + estadoIngreso + "}";
    }
}
