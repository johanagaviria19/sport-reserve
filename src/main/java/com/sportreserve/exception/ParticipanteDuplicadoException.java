package com.sportreserve.exception;

public class ParticipanteDuplicadoException extends BusinessException {

    public ParticipanteDuplicadoException(Long personaId, Long reservaId) {
        super(String.format("La persona %d ya se encuentra registrada en la reserva %d", personaId, reservaId));
    }
}
