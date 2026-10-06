package com.sportreserve.service;

import com.sportreserve.dto.request.ReservaRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.dto.response.ParticipanteResponse;
import com.sportreserve.dto.response.ReservaResponse;
import com.sportreserve.enums.EstadoPago;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.exception.ReservaNoDisponibleException;
import com.sportreserve.model.Pago;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Reserva;
import com.sportreserve.observer.ReservaEvent;
import com.sportreserve.repository.EscenarioRepository;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final EscenarioRepository escenarioRepository;
    private final ParticipacionReservaRepository participacionReservaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservaService(ReservaRepository reservaRepository,
                          PersonaRepository personaRepository,
                          EscenarioRepository escenarioRepository,
                          ParticipacionReservaRepository participacionReservaRepository,
                          ApplicationEventPublisher eventPublisher) {
        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.escenarioRepository = escenarioRepository;
        this.participacionReservaRepository = participacionReservaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ReservaResponse crear(ReservaRequest request) {
        var escenario = escenarioRepository.findById(request.getEscenarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Escenario", request.getEscenarioId()));

        var responsable = personaRepository.findById(request.getResponsableId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", request.getResponsableId()));

        LocalDate fecha = request.getFecha();
        LocalTime horaInicio = request.getHoraInicio();
        LocalTime horaFin = request.getHoraFin();

        if (!horaInicio.isBefore(horaFin)) {
            throw new ReservaInvalidaException("La hora de inicio debe ser anterior a la hora de fin");
        }

        long minutos = Duration.between(horaInicio, horaFin).toMinutes();
        BigDecimal duracionHoras = BigDecimal.valueOf(minutos)
                .divide(BigDecimal.valueOf(60.0), 4, RoundingMode.HALF_UP);

        if (duracionHoras.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReservaInvalidaException("La reserva debe tener una duración valida mayor a cero");
        }

        List<Reserva> reservasConflicto = reservaRepository.findReservasConflictoHorario(
                request.getEscenarioId(), fecha, horaInicio, horaFin, EstadoReserva.CANCELADA);

        if (!reservasConflicto.isEmpty()) {
            throw new ReservaNoDisponibleException(
                    request.getEscenarioId(),
                    fecha.toString(),
                    horaInicio.toString(),
                    horaFin.toString());
        }

        Reserva reserva = new Reserva();
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(escenario);
        reserva.setResponsable(responsable);

        BigDecimal valorTotal = escenario.getPrecioPorHora()
                .multiply(duracionHoras)
                .setScale(2, RoundingMode.HALF_UP);

        Pago pago = new Pago(valorTotal);
        reserva.setPago(pago);

        Reserva reservaGuardada = reservaRepository.save(reserva);

        RolParticipacion rolResponsable = request.getRolResponsable() != null
                ? request.getRolResponsable()
                : RolParticipacion.JUGADOR;
        ParticipacionReserva participacion = new ParticipacionReserva(responsable, reservaGuardada, rolResponsable);
        participacionReservaRepository.save(participacion);

        eventPublisher.publishEvent(new ReservaEvent(reservaGuardada.getId(), reservaGuardada.getEstado()));

        return toResponse(reservaGuardada);
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
        return toResponse(reserva);
    }

    @Transactional(readOnly = true)
    public Reserva obtenerEntidadPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarTodos() {
        return reservaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));

        if (reserva.getEstado() == EstadoReserva.EN_USO || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReservaInvalidaException(
                    String.format("No se puede cancelar una reserva en estado %s", reserva.getEstado()));
        }

        reserva.cancelar();
        Reserva reservaGuardada = reservaRepository.save(reserva);

        eventPublisher.publishEvent(new ReservaEvent(reservaGuardada.getId(), reservaGuardada.getEstado()));

        return toResponse(reservaGuardada);
    }

    private ReservaResponse toResponse(Reserva reserva) {
        ReservaResponse response = new ReservaResponse();
        response.setId(reserva.getId());
        response.setFecha(reserva.getFecha());
        response.setHoraInicio(reserva.getHoraInicio());
        response.setHoraFin(reserva.getHoraFin());
        response.setEstado(reserva.getEstado());

        if (reserva.getEscenario() != null) {
            response.setEscenarioId(reserva.getEscenario().getId());
            response.setEscenarioNombre(reserva.getEscenario().getNombre());
            response.setTipoEscenario(reserva.getEscenario().getTipo());
        }

        if (reserva.getResponsable() != null) {
            response.setResponsableId(reserva.getResponsable().getId());
            response.setResponsableNombre(reserva.getResponsable().getNombreCompleto());
        }

        if (reserva.getParticipaciones() != null && reserva.getResponsable() != null) {
            reserva.getParticipaciones().stream()
                    .filter(p -> p.getPersona() != null
                            && p.getPersona().getId().equals(reserva.getResponsable().getId()))
                    .findFirst()
                    .ifPresent(p -> response.setRolResponsable(p.getRol()));
        }

        if (reserva.getParticipaciones() != null) {
            List<ParticipanteResponse> participantes = reserva.getParticipaciones().stream()
                    .map(this::toParticipanteResponse)
                    .collect(Collectors.toList());
            response.setParticipantes(participantes);
        }

        if (reserva.getPago() != null) {
            response.setPago(toPagoResponse(reserva.getPago()));
        }

        return response;
    }

    private ParticipanteResponse toParticipanteResponse(ParticipacionReserva p) {
        ParticipanteResponse pr = new ParticipanteResponse();
        if (p.getPersona() != null) {
            pr.setPersonaId(p.getPersona().getId());
            pr.setNombreCompleto(p.getPersona().getNombreCompleto());
            pr.setIdentificacion(p.getPersona().getIdentificacion());
        }
        pr.setRol(p.getRol());
        pr.setEstadoIngreso(p.getEstadoIngreso());
        pr.setFechaHoraIngreso(p.getFechaHoraIngreso());
        return pr;
    }

    private PagoResponse toPagoResponse(Pago pago) {
        PagoResponse pr = new PagoResponse();
        pr.setId(pago.getId());
        pr.setValorTotal(pago.getValorTotal());
        pr.setValorPagado(pago.getValorPagado());
        pr.setSaldoPendiente(pago.getSaldoPendiente());
        pr.setEstado(pago.getEstado());
        pr.setMetodoPago(pago.getMetodoPago());
        return pr;
    }
}
