package com.sportreserve.service;

import com.sportreserve.dto.request.ParticipanteRequest;
import com.sportreserve.dto.response.ParticipanteResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.CapacidadJugadoresExcedidaException;
import com.sportreserve.exception.ParticipanteDuplicadoException;
import com.sportreserve.exception.ParticipanteNoRegistradoException;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.model.Escenario;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.EscenarioRepository;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipacionService {

    private final ParticipacionReservaRepository participacionReservaRepository;
    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final EscenarioRepository escenarioRepository;

    public ParticipacionService(ParticipacionReservaRepository participacionReservaRepository,
                                ReservaRepository reservaRepository,
                                PersonaRepository personaRepository,
                                EscenarioRepository escenarioRepository) {
        this.participacionReservaRepository = participacionReservaRepository;
        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.escenarioRepository = escenarioRepository;
    }

    @Transactional
    public ParticipanteResponse agregarParticipante(Long reservaId, ParticipanteRequest request) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.EN_USO
                || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReservaInvalidaException(
                    String.format("No se pueden agregar participantes a una reserva en estado %s", reserva.getEstado()));
        }

        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", request.getPersonaId()));

        if (participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, persona.getId())) {
            throw new ParticipanteDuplicadoException(persona.getId(), reservaId);
        }

        if (request.getRol() == RolParticipacion.JUGADOR) {
            Escenario escenario = reserva.getEscenario();
            int jugadoresActuales = participacionReservaRepository
                    .findByReservaIdAndRol(reservaId, RolParticipacion.JUGADOR).size();
            if (jugadoresActuales >= escenario.getCapacidadJugadores()) {
                throw new CapacidadJugadoresExcedidaException(escenario.getCapacidadJugadores(), jugadoresActuales);
            }
        }

        ParticipacionReserva participacion = new ParticipacionReserva(persona, reserva, request.getRol());
        participacion = participacionReservaRepository.save(participacion);

        return toResponse(participacion);
    }

    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listarParticipantes(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        return participacionReservaRepository.findByReservaId(reservaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParticipanteResponse buscarParticipante(Long reservaId, Long personaId) {
        ParticipacionReserva participacion = participacionReservaRepository
                .findByReservaIdAndPersonaId(reservaId, personaId)
                .orElseThrow(() -> new ParticipanteNoRegistradoException(personaId, reservaId));

        return toResponse(participacion);
    }

    @Transactional(readOnly = true)
    public ParticipacionReserva obtenerParticipacionEntidad(Long reservaId, Long personaId) {
        return participacionReservaRepository
                .findByReservaIdAndPersonaId(reservaId, personaId)
                .orElseThrow(() -> new ParticipanteNoRegistradoException(personaId, reservaId));
    }

    @Transactional
    public void eliminarParticipante(Long reservaId, Long personaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        if (reserva.getEstado() == EstadoReserva.EN_USO
                || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReservaInvalidaException(
                    String.format("No se puede eliminar participante de reserva en estado %s", reserva.getEstado()));
        }

        ParticipacionReserva participacion = participacionReservaRepository
                .findByReservaIdAndPersonaId(reservaId, personaId)
                .orElseThrow(() -> new ParticipanteNoRegistradoException(personaId, reservaId));

        if (reserva.getResponsable().getId().equals(personaId)) {
            throw new ReservaInvalidaException("No se puede eliminar al responsable de la reserva");
        }

        participacionReservaRepository.delete(participacion);
    }

    private ParticipanteResponse toResponse(ParticipacionReserva p) {
        ParticipanteResponse response = new ParticipanteResponse();
        response.setPersonaId(p.getPersona().getId());
        response.setNombreCompleto(p.getPersona().getNombreCompleto());
        response.setIdentificacion(p.getPersona().getIdentificacion());
        response.setRol(p.getRol());
        response.setEstadoIngreso(p.getEstadoIngreso());
        response.setFechaHoraIngreso(p.getFechaHoraIngreso());
        return response;
    }
}
