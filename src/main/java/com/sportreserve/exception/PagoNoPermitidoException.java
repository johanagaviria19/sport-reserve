package com.sportreserve.exception;

import com.sportreserve.enums.EstadoReserva;

public class PagoNoPermitidoException extends BusinessException {

    public PagoNoPermitidoException(String message) {
        super(message);
    }

    public PagoNoPermitidoException(Long reservaId, EstadoReserva estado) {
        super(String.format("No se permite realizar pagos en la reserva %d. Estado actual: %s", reservaId, estado));
    }
}
