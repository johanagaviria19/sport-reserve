package com.sportreserve.exception;

public class CapacidadJugadoresExcedidaException extends BusinessException {

    public CapacidadJugadoresExcedidaException(int capacidadMaxima, long jugadoresActuales) {
        super(String.format("Capacidad maxima de jugadores excedida. Limite: %d, Jugadores actuales: %d",
                capacidadMaxima, jugadoresActuales));
    }
}
