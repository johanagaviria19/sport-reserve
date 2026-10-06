package com.sportreserve.service;

import com.sportreserve.dto.request.IngresoRequest;
import com.sportreserve.dto.response.IngresoResponse;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.IngresoNoPermitidoException;
import com.sportreserve.exception.ParticipanteNoRegistradoException;
import com.sportreserve.exception.PersonaNoAutorizadaException;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ControlAccesoService {

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final ParticipacionReservaRepository participacionReservaRepository;

    public ControlAccesoService(ReservaRepository reservaRepository,
                                PersonaRepository personaRepository,
                                ParticipacionReservaRepository participacionReservaRepository) {
        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.participacionReservaRepository = participacionReservaRepository;
    }

    @Transactional
    public IngresoResponse registrarIngreso(Long reservaId, IngresoRequest request) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", request.getPersonaId()));

        ParticipacionReserva participacion = participacionReservaRepository
                .findByReservaIdAndPersonaId(reservaId, persona.getId())
                .orElseThrow(() -> new ParticipanteNoRegistradoException(persona.getId(), reservaId));

        if (RolParticipacion.ESPECTADOR == participacion.getRol()) {
            throw new PersonaNoAutorizadaException(persona.getId(), participacion.getRol(), RolParticipacion.JUGADOR);
        }

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA && reserva.getEstado() != EstadoReserva.EN_USO) {
            throw new IngresoNoPermitidoException(
                    String.format("No se permite el ingreso. Reserva en estado %s", reserva.getEstado()));
        }

        if (participacion.haIngresado()) {
            throw new IngresoNoPermitidoException(
                    String.format("La persona %s ya registró ingreso en esta reserva", persona.getId()));
        }

        participacion.registrarIngreso();
        participacionReservaRepository.save(participacion);

        return toResponse(participacion);
    }

    private IngresoResponse toResponse(ParticipacionReserva p) {
        IngresoResponse response = new IngresoResponse();
        response.setPersonaId(p.getPersona().getId());
        response.setNombreCompleto(p.getPersona().getNombreCompleto());
        response.setRol(p.getRol());
        response.setEstadoIngreso(p.getEstadoIngreso());
        response.setFechaHoraIngreso(p.getFechaHoraIngreso());
        return response;
    }
}
