package com.sportreserve.dto.request;

import com.sportreserve.enums.TipoEscenario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class EscenarioRequest {

    @NotBlank(message = "El nombre del escenario es obligatorio")
    private String nombre;

    @NotNull(message = "El precio por hora es obligatorio")
    @Positive(message = "El precio por hora debe ser positivo")
    private BigDecimal precioPorHora;

    @NotNull(message = "La capacidad de jugadores es obligatoria")
    @Positive(message = "La capacidad de jugadores debe ser positiva")
    private Integer capacidadJugadores;

    @NotNull(message = "El tipo de escenario es obligatorio")
    private TipoEscenario tipo;

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

    public void setTipo(TipoEscenario tipo) {
        this.tipo = tipo;
    }
}
