package com.sportreserve.exception;

public class ParticipanteNoRegistradoException extends BusinessException {

    public ParticipanteNoRegistradoException(Long personaId, Long reservaId) {
        super(String.format("La persona %d no esta registrada como participante de la reserva %d", personaId, reservaId));
    }
}
