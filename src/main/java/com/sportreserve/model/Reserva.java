package com.sportreserve.model;

import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Persona responsable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escenario_id", nullable = false)
    private Escenario escenario;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParticipacionReserva> participaciones = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", unique = true)
    private Pago pago;

    public Reserva() {
    }

    public Reserva(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Persona responsable, Escenario escenario, Pago pago) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = EstadoReserva.PENDIENTE;
        this.responsable = responsable;
        this.escenario = escenario;
        this.pago = pago;
    }

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

    public Persona getResponsable() {
        return responsable;
    }

    public void setResponsable(Persona responsable) {
        this.responsable = responsable;
    }

    public Escenario getEscenario() {
        return escenario;
    }

    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }

    public List<ParticipacionReserva> getParticipaciones() {
        return participaciones;
    }

    public void setParticipaciones(List<ParticipacionReserva> participaciones) {
        this.participaciones = participaciones;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public void agregarParticipante(Persona persona, RolParticipacion rol) {
        ParticipacionReserva participacion = new ParticipacionReserva(persona, this, rol);
        this.participaciones.add(participacion);
    }

    public void eliminarParticipante(Long personaId) {
        this.participaciones.removeIf(p -> p.getPersona() != null && p.getPersona().getId().equals(personaId));
    }

    public boolean tieneParticipante(Long personaId) {
        return this.participaciones.stream()
                .anyMatch(p -> p.getPersona() != null && p.getPersona().getId().equals(personaId));
    }

    public long contarJugadores() {
        return this.participaciones.stream()
                .filter(p -> p.getRol() == RolParticipacion.JUGADOR)
                .count();
    }

    public long contarEspectadores() {
        return this.participaciones.stream()
                .filter(p -> p.getRol() == RolParticipacion.ESPECTADOR)
                .count();
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean esCancelable() {
        return this.estado == EstadoReserva.PENDIENTE || this.estado == EstadoReserva.CONFIRMADA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva reserva)) return false;
        return id != null && Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : System.identityHashCode(this));
    }

    @Override
    public String toString() {
        return "Reserva{id=" + id + ", fecha=" + fecha + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin +
                ", estado=" + estado + ", responsableId=" + (responsable != null ? responsable.getId() : null) +
                ", escenarioId=" + (escenario != null ? escenario.getId() : null) + "}";
    }
}
