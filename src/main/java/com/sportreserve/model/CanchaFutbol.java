package com.sportreserve.model;

import com.sportreserve.enums.TipoEscenario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "canchas_futbol")
public class CanchaFutbol extends Escenario {

    public CanchaFutbol() {
    }

    public CanchaFutbol(String nombre, BigDecimal precioPorHora, Integer capacidadJugadores) {
        super(nombre, precioPorHora, capacidadJugadores, TipoEscenario.FUTBOL);
    }

    @Override
    public BigDecimal calcularPrecio(int horas) {
        if (horas <= 0) {
            return BigDecimal.ZERO;
        }
        return getPrecioPorHora().multiply(BigDecimal.valueOf(horas));
    }
}
