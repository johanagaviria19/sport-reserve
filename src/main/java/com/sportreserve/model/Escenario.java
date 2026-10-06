package com.sportreserve.model;

import com.sportreserve.enums.TipoEscenario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "escenarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Escenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del escenario es obligatorio")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotNull(message = "El precio por hora es obligatorio")
    @Positive(message = "El precio por hora debe ser positivo")
    @Column(name = "precio_por_hora", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioPorHora;

    @NotNull(message = "La capacidad de jugadores es obligatoria")
    @Positive(message = "La capacidad de jugadores debe ser positiva")
    @Column(name = "capacidad_jugadores", nullable = false)
    private Integer capacidadJugadores;

    @NotNull(message = "El tipo de escenario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoEscenario tipo;

    protected Escenario() {
    }

    protected Escenario(String nombre, BigDecimal precioPorHora, Integer capacidadJugadores, TipoEscenario tipo) {
        this.nombre = nombre;
        this.precioPorHora = precioPorHora;
        this.capacidadJugadores = capacidadJugadores;
        this.tipo = tipo;
    }

    public abstract BigDecimal calcularPrecio(int horas);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(BigDecimal precioPorHora) {
        this.precioPorHora = precioPorHora;
    }

    public Integer getCapacidadJugadores() {
        return capacidadJugadores;
    }

    public void setCapacidadJugadores(Integer capacidadJugadores) {
        this.capacidadJugadores = capacidadJugadores;
    }

    public TipoEscenario getTipo() {
        return tipo;
    }

    protected void setTipo(TipoEscenario tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Escenario that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : System.identityHashCode(this));
    }

    @Override
    public String toString() {
        return "Escenario{id=" + id + ", tipo=" + tipo + ", nombre='" + nombre + "', capacidad=" + capacidadJugadores + "}";
    }
}
