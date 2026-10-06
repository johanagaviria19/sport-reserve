package com.sportreserve.service;

import com.sportreserve.dto.request.IngresoRequest;
import com.sportreserve.dto.response.IngresoResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.exception.IngresoNoPermitidoException;
import com.sportreserve.exception.ParticipanteNoRegistradoException;
import com.sportreserve.exception.PersonaNoAutorizadaException;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.Pago;
import com.sportreserve.model.ParticipacionReserva;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.ParticipacionReservaRepository;
import com.sportreserve.repository.PersonaRepository;
import com.sportreserve.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControlAccesoServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private ParticipacionReservaRepository participacionReservaRepository;

    @InjectMocks
    private ControlAccesoService controlAccesoService;

    private Persona crearPersonaBase(Long id) {
        Persona persona = new Persona("123456789" + id, "Nombre" + id, "Apellido" + id,
                "300123456" + id, "persona" + id + "@correo.com");
        persona.setId(id);
        return persona;
    }

    private Reserva crearReservaBase(Long id, EstadoReserva estado) {
        Persona responsable = crearPersonaBase(10L);

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 22);
        cancha.setId(5L);

        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado(estado);
        reserva.setResponsable(responsable);
        reserva.setEscenario(cancha);
        reserva.setPago(new Pago(new BigDecimal("120000")));
        return reserva;
    }

    private IngresoRequest crearIngresoRequest(Long personaId) {
        IngresoRequest request = new IngresoRequest();
        request.setPersonaId(personaId);
        return request;
    }

    @Test
    void registrarIngreso_JugadorRegistradoConfimada_IngresoExitoso() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.CONFIRMADA);
        Persona persona = crearPersonaBase(5L);
        ParticipacionReserva participacion = new ParticipacionReserva(persona, reserva, RolParticipacion.JUGADOR);
        participacion.setId(1L);
        participacion.setEstadoIngreso(EstadoIngreso.REGISTRADO);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(5L)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.findByReservaIdAndPersonaId(1L, 5L)).thenReturn(Optional.of(participacion));
        when(participacionReservaRepository.save(any(ParticipacionReserva.class))).thenReturn(participacion);

        IngresoRequest request = crearIngresoRequest(5L);

        IngresoResponse response = controlAccesoService.registrarIngreso(1L, request);

        assertEquals(EstadoIngreso.INGRESO, response.getEstadoIngreso());
        assertNotNull(response.getFechaHoraIngreso());
        assertEquals(RolParticipacion.JUGADOR, response.getRol());
    }

    @Test
    void registrarIngreso_PersonaNoRegistrada_LanzaParticipanteNoRegistrado() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.CONFIRMADA);
        Persona persona = crearPersonaBase(5L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(5L)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.findByReservaIdAndPersonaId(1L, 5L)).thenReturn(Optional.empty());

        IngresoRequest request = crearIngresoRequest(5L);

        assertThrows(ParticipanteNoRegistradoException.class,
                () -> controlAccesoService.registrarIngreso(1L, request));
    }

    @Test
    void registrarIngreso_Espectador_LanzaPersonaNoAutorizada() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.CONFIRMADA);
        Persona persona = crearPersonaBase(5L);
        ParticipacionReserva participacion = new ParticipacionReserva(persona, reserva, RolParticipacion.ESPECTADOR);
        participacion.setId(1L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(5L)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.findByReservaIdAndPersonaId(1L, 5L)).thenReturn(Optional.of(participacion));

        IngresoRequest request = crearIngresoRequest(5L);

        PersonaNoAutorizadaException exception = assertThrows(PersonaNoAutorizadaException.class,
                () -> controlAccesoService.registrarIngreso(1L, request));
        assertTrue(exception.getMessage().contains("JUGADOR") || exception.getMessage().contains("ESPECTADOR"));
    }

    @Test
    void registrarIngreso_ReservaPendiente_NoPermiteIngreso() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE);
        Persona persona = crearPersonaBase(5L);
        ParticipacionReserva participacion = new ParticipacionReserva(persona, reserva, RolParticipacion.JUGADOR);
        participacion.setId(1L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(personaRepository.findById(5L)).thenReturn(Optional.of(persona));
        when(participacionReservaRepository.findByReservaIdAndPersonaId(1L, 5L)).thenReturn(Optional.of(participacion));

        IngresoRequest request = crearIngresoRequest(5L);

        IngresoNoPermitidoException exception = assertThrows(IngresoNoPermitidoException.class,
                () -> controlAccesoService.registrarIngreso(1L, request));
        assertTrue(exception.getMessage().toLowerCase().contains("estado"));
    }
}
