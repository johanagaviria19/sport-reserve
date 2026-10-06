package com.sportreserve.service;

import com.sportreserve.dto.request.ReservaRequest;
import com.sportreserve.dto.response.ReservaResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.exception.ReservaNoDisponibleException;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.observer.ReservaEvent;
import com.sportreserve.repository.EscenarioRepository;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EscenarioRepository escenarioRepository;

    @Mock
    private ParticipacionReservaRepository participacionReservaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReserva_CuandoDatosValidos_CreaReservaYParticipacion() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);
        Long escenarioId = 1L;
        Long responsableId = 2L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);
        responsable.setNombre("Juan");
        responsable.setApellido("Perez");

        ReservaRequest request = new ReservaRequest();
        request.setFecha(fecha);
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));
        when(reservaRepository.findReservasConflictoHorario(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        when(participacionReservaRepository.save(any(ParticipacionReserva.class))).thenAnswer(invocation -> {
            ParticipacionReserva p = invocation.getArgument(0);
            p.setId(1L);
            if (p.getReserva() != null) {
                p.getReserva().getParticipaciones().add(p);
            }
            return p;
        });

        ReservaResponse response = reservaService.crear(request);

        assertNotNull(response);
        assertEquals(EstadoReserva.PENDIENTE, response.getEstado());
        assertEquals(escenarioId, response.getEscenarioId());
        assertEquals(responsableId, response.getResponsableId());
        assertEquals(RolParticipacion.JUGADOR, response.getRolResponsable());
        assertNotNull(response.getPago());
        assertEquals(0, new BigDecimal("120000").compareTo(response.getPago().getValorTotal()));

        verify(reservaRepository, times(1)).save(any(Reserva.class));
        verify(participacionReservaRepository, times(1)).save(any(ParticipacionReserva.class));
    }

    @Test
    void crearReserva_CuandoDatosValidos_PublicaReservaEventConIdYPendiente() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);
        Long escenarioId = 1L;
        Long responsableId = 2L;
        Long reservaEsperadaId = 50L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);
        responsable.setNombre("Juan");
        responsable.setApellido("Perez");

        ReservaRequest request = new ReservaRequest();
        request.setFecha(fecha);
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));
        when(reservaRepository.findReservasConflictoHorario(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(reservaEsperadaId);
            return r;
        });

        when(participacionReservaRepository.save(any(ParticipacionReserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservaService.crear(request);

        ArgumentCaptor<ReservaEvent> captor = ArgumentCaptor.forClass(ReservaEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        ReservaEvent eventCapturado = captor.getValue();
        assertEquals(reservaEsperadaId, eventCapturado.getReservaId());
        assertEquals(EstadoReserva.PENDIENTE, eventCapturado.getEstado());
        assertNotNull(eventCapturado.getFechaHora());
    }

    @Test
    void crearReserva_CuandoEscenarioNoExiste_LanzaRecursoNoEncontrado() {
        Long escenarioId = 99L;

        ReservaRequest request = new ReservaRequest();
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(12, 0));
        request.setEscenarioId(escenarioId);
        request.setResponsableId(2L);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.crear(request));
    }

    @Test
    void crearReserva_CuandoResponsableNoExiste_LanzaRecursoNoEncontrado() {
        Long escenarioId = 1L;
        Long responsableId = 99L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        ReservaRequest request = new ReservaRequest();
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(12, 0));
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.crear(request));
        assertTrue(ex.getMessage().contains("Persona"));
    }

    @Test
    void crearReserva_CuandoHoraFinAntesHoraInicio_LanzaReservaInvalida() {
        Long escenarioId = 1L;
        Long responsableId = 2L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);

        ReservaRequest request = new ReservaRequest();
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(12, 0));
        request.setHoraFin(LocalTime.of(10, 0));
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));

        assertThrows(ReservaInvalidaException.class, () -> reservaService.crear(request));
    }

    @Test
    void crearReserva_CuandoConflictoHorario_LanzaReservaNoDisponible() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);
        Long escenarioId = 1L;
        Long responsableId = 2L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);

        Reserva reservaConflicto = new Reserva();
        reservaConflicto.setId(99L);

        ReservaRequest request = new ReservaRequest();
        request.setFecha(fecha);
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));
        when(reservaRepository.findReservasConflictoHorario(any(), any(), any(), any(), any()))
                .thenReturn(List.of(reservaConflicto));

        assertThrows(ReservaNoDisponibleException.class, () -> reservaService.crear(request));
    }

    @Test
    void crearReserva_CuandoReservaCanceladaExiste_NoBloquea() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);
        Long escenarioId = 1L;
        Long responsableId = 2L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);
        responsable.setNombre("Juan");
        responsable.setApellido("Perez");

        ReservaRequest request = new ReservaRequest();
        request.setFecha(fecha);
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));
        when(reservaRepository.findReservasConflictoHorario(
                any(), any(), any(), any(), eq(EstadoReserva.CANCELADA)))
                .thenReturn(Collections.emptyList());

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        when(participacionReservaRepository.save(any(ParticipacionReserva.class))).thenAnswer(invocation -> {
            ParticipacionReserva p = invocation.getArgument(0);
            p.setId(1L);
            if (p.getReserva() != null) {
                p.getReserva().getParticipaciones().add(p);
            }
            return p;
        });

        ReservaResponse response = reservaService.crear(request);

        assertNotNull(response);
    }

    @Test
    void crearReserva_ResponsableRegistradoAutomaticamente() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);
        Long escenarioId = 1L;
        Long responsableId = 2L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 10);
        cancha.setId(escenarioId);

        Persona responsable = new Persona();
        responsable.setId(responsableId);
        responsable.setNombre("Juan");
        responsable.setApellido("Perez");

        ReservaRequest request = new ReservaRequest();
        request.setFecha(fecha);
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setEscenarioId(escenarioId);
        request.setResponsableId(responsableId);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        when(escenarioRepository.findById(escenarioId)).thenReturn(Optional.of(cancha));
        when(personaRepository.findById(responsableId)).thenReturn(Optional.of(responsable));
        when(reservaRepository.findReservasConflictoHorario(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        when(participacionReservaRepository.save(any(ParticipacionReserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservaService.crear(request);

        ArgumentCaptor<ParticipacionReserva> captor =
                ArgumentCaptor.forClass(ParticipacionReserva.class);
        verify(participacionReservaRepository).save(captor.capture());

        ParticipacionReserva participacionCapturada = captor.getValue();
        assertEquals(responsableId, participacionCapturada.getPersona().getId());
        assertEquals(RolParticipacion.JUGADOR, participacionCapturada.getRol());
        assertEquals(EstadoIngreso.REGISTRADO, participacionCapturada.getEstadoIngreso());
    }

    @Test
    void cancelarReserva_CuandoEstadoPendiente_CambiaEstado() {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.cancelar();
            return r;
        });

        ReservaResponse response = reservaService.cancelar(reservaId);

        assertEquals(EstadoReserva.CANCELADA, response.getEstado());
    }

    @Test
    void cancelarReserva_CuandoExitosa_PublicaReservaEventConIdYCancelada() {
        Long reservaId = 20L;
        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.cancelar();
            return r;
        });

        reservaService.cancelar(reservaId);

        ArgumentCaptor<ReservaEvent> captor = ArgumentCaptor.forClass(ReservaEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        ReservaEvent eventCapturado = captor.getValue();
        assertEquals(reservaId, eventCapturado.getReservaId());
        assertEquals(EstadoReserva.CANCELADA, eventCapturado.getEstado());
        assertNotNull(eventCapturado.getFechaHora());
    }

    @Test
    void cancelarReserva_CuandoEnUso_LanzaReservaInvalida() {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.EN_USO);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        assertThrows(ReservaInvalidaException.class, () -> reservaService.cancelar(reservaId));
    }

    @Test
    void cancelarReserva_CuandoFinalizada_LanzaReservaInvalida() {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.FINALIZADA);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        assertThrows(ReservaInvalidaException.class, () -> reservaService.cancelar(reservaId));
    }
}
