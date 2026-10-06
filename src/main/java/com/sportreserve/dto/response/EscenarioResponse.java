package com.sportreserve.dto.response;

import com.sportreserve.enums.TipoEscenario;

import java.math.BigDecimal;

public class EscenarioResponse {

    private Long id;
    private String nombre;
    private BigDecimal precioPorHora;
    private Integer capacidadJugadores;
    private TipoEscenario tipo;

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

    public void setTipo(TipoEscenario tipo) {
        this.tipo = tipo;
    }
}
