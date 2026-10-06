package com.sportreserve.exception;

public class ReservaNoDisponibleException extends BusinessException {

    public ReservaNoDisponibleException(String message) {
        super(message);
    }

    public ReservaNoDisponibleException(Long escenarioId, String fecha, String horaInicio, String horaFin) {
        super(String.format("El escenario %d no esta disponible el %s entre %s y %s",
                escenarioId, fecha, horaInicio, horaFin));
    }
}
