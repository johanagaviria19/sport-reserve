package com.sportreserve.service;

import com.sportreserve.dto.request.ParticipanteRequest;
import com.sportreserve.dto.response.ParticipanteResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.CapacidadJugadoresExcedidaException;
import com.sportreserve.exception.ParticipanteDuplicadoException;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.EscenarioRepository;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipacionServiceTest {

    @Mock
    private ParticipacionReservaRepository participacionReservaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EscenarioRepository escenarioRepository;

    @InjectMocks
    private ParticipacionService participacionService;

    @Test
    void agregarParticipante_Jugador_CuandoHayCapacidad_RegistraExitoso() {
        Long reservaId = 1L;
        Long personaId = 5L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 10);
        cancha.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(cancha);

        Persona persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Carlos");
        persona.setApellido("Gomez");
        persona.setIdentificacion("12345");

        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(personaId);
        request.setRol(RolParticipacion.JUGADOR);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(personaId)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, personaId))
                .thenReturn(false);
        when(participacionReservaRepository.findByReservaIdAndRol(reservaId, RolParticipacion.JUGADOR))
                .thenReturn(Collections.emptyList());
        when(participacionReservaRepository.save(any(ParticipacionReserva.class)))
                .thenAnswer(invocation -> {
                    ParticipacionReserva p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ParticipanteResponse response = participacionService.agregarParticipante(reservaId, request);

        assertNotNull(response);
        assertEquals(RolParticipacion.JUGADOR, response.getRol());
        assertEquals(EstadoIngreso.REGISTRADO, response.getEstadoIngreso());
        assertEquals(personaId, response.getPersonaId());
    }

    @Test
    void agregarParticipante_Espectador_NoVerificaCapacidad() {
        Long reservaId = 1L;
        Long personaId = 5L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 0);
        cancha.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(cancha);

        Persona persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Carlos");
        persona.setApellido("Gomez");
        persona.setIdentificacion("12345");

        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(personaId);
        request.setRol(RolParticipacion.ESPECTADOR);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(personaId)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, personaId))
                .thenReturn(false);
        when(participacionReservaRepository.save(any(ParticipacionReserva.class)))
                .thenAnswer(invocation -> {
                    ParticipacionReserva p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ParticipanteResponse response = participacionService.agregarParticipante(reservaId, request);

        assertNotNull(response);
        assertEquals(RolParticipacion.ESPECTADOR, response.getRol());
        assertEquals(EstadoIngreso.REGISTRADO, response.getEstadoIngreso());

        verify(participacionReservaRepository, never())
                .findByReservaIdAndRol(eq(reservaId), eq(RolParticipacion.JUGADOR));
    }

    @Test
    void agregarParticipante_CuandoDuplicado_LanzaParticipanteDuplicado() {
        Long reservaId = 1L;
        Long personaId = 5L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 10);
        cancha.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(cancha);

        Persona persona = new Persona();
        persona.setId(personaId);

        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(personaId);
        request.setRol(RolParticipacion.JUGADOR);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(personaId)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, personaId))
                .thenReturn(true);

        assertThrows(ParticipanteDuplicadoException.class,
                () -> participacionService.agregarParticipante(reservaId, request));
    }

    @Test
    void agregarParticipante_Jugador_CapacidadExcedida_LanzaExcepcion() {
        Long reservaId = 1L;
        Long personaId = 5L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 2);
        cancha.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(cancha);

        Persona persona = new Persona();
        persona.setId(personaId);

        Persona jugador1 = new Persona();
        jugador1.setId(10L);
        Persona jugador2 = new Persona();
        jugador2.setId(11L);

        ParticipacionReserva p1 = new ParticipacionReserva(jugador1, reserva, RolParticipacion.JUGADOR);
        ParticipacionReserva p2 = new ParticipacionReserva(jugador2, reserva, RolParticipacion.JUGADOR);
        List<ParticipacionReserva> jugadoresActuales = List.of(p1, p2);

        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(personaId);
        request.setRol(RolParticipacion.JUGADOR);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(personaId)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, personaId))
                .thenReturn(false);
        when(participacionReservaRepository.findByReservaIdAndRol(reservaId, RolParticipacion.JUGADOR))
                .thenReturn(jugadoresActuales);

        assertThrows(CapacidadJugadoresExcedidaException.class,
                () -> participacionService.agregarParticipante(reservaId, request));
    }

    @Test
    void agregarParticipante_EspectadoresNoConsumenCapacidad() {
        Long reservaId = 1L;
        Long personaId = 5L;

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 2);
        cancha.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setEscenario(cancha);

        Persona persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Ana");
        persona.setApellido("Diaz");
        persona.setIdentificacion("54321");

        Persona jugador1 = new Persona();
        jugador1.setId(10L);
        Persona jugador2 = new Persona();
        jugador2.setId(11L);

        ParticipacionReserva p1 = new ParticipacionReserva(jugador1, reserva, RolParticipacion.JUGADOR);
        ParticipacionReserva p2 = new ParticipacionReserva(jugador2, reserva, RolParticipacion.JUGADOR);
        List<ParticipacionReserva> jugadoresActuales = List.of(p1, p2);

        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(personaId);
        request.setRol(RolParticipacion.ESPECTADOR);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(personaId)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.existsByReservaIdAndPersonaId(reservaId, personaId))
                .thenReturn(false);
        when(participacionReservaRepository.save(any(ParticipacionReserva.class)))
                .thenAnswer(invocation -> {
                    ParticipacionReserva p = invocation.getArgument(0);
                    p.setId(99L);
                    return p;
                });

        ParticipanteResponse response = assertDoesNotThrow(
                () -> participacionService.agregarParticipante(reservaId, request));

        assertNotNull(response);
        assertEquals(RolParticipacion.ESPECTADOR, response.getRol());
        assertEquals(EstadoIngreso.REGISTRADO, response.getEstadoIngreso());
    }
}
