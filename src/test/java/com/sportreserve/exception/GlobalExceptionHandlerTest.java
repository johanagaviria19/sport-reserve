package com.sportreserve.exception;

import com.sportreserve.dto.response.ErrorResponse;
import com.sportreserve.enums.RolParticipacion;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(mockRequest.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void recursoNoEncontrado_DebeRetornar404() {
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("Persona", 10L);

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNoEncontrado(ex, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("NOT_FOUND", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("10"));
    }

    @Test
    void identificacionDuplicada_DebeRetornar409() {
        IdentificacionDuplicadaException ex = new IdentificacionDuplicadaException("12345");

        ResponseEntity<ErrorResponse> response = handler.handleIdentificacionDuplicada(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("CONFLICT", response.getBody().getError());
    }

    @Test
    void reservaNoDisponible_DebeRetornar409() {
        ReservaNoDisponibleException ex = new ReservaNoDisponibleException(1L, "2026-01-01", "10:00", "12:00");

        ResponseEntity<ErrorResponse> response = handler.handleReservaNoDisponible(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("CONFLICT", response.getBody().getError());
    }

    @Test
    void participanteNoRegistrado_DebeRetornar403() {
        ParticipanteNoRegistradoException ex = new ParticipanteNoRegistradoException(99L, 50L);

        ResponseEntity<ErrorResponse> response = handler.handleParticipanteNoRegistrado(ex, mockRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("FORBIDDEN", response.getBody().getError());
    }

    @Test
    void personaNoAutorizada_DebeRetornar403() {
        PersonaNoAutorizadaException ex = new PersonaNoAutorizadaException(5L, RolParticipacion.ESPECTADOR, RolParticipacion.JUGADOR);

        ResponseEntity<ErrorResponse> response = handler.handlePersonaNoAutorizada(ex, mockRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("FORBIDDEN", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("ESPECTADOR"));
    }

    @Test
    void capacidadJugadoresExcedida_DebeRetornar409() {
        CapacidadJugadoresExcedidaException ex = new CapacidadJugadoresExcedidaException(10, 11);

        ResponseEntity<ErrorResponse> response = handler.handleCapacidadJugadoresExcedida(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("CONFLICT", response.getBody().getError());
    }

    @Test
    void pagoInvalido_DebeRetornar400() {
        PagoInvalidoException ex = new PagoInvalidoException("Monto supera saldo");

        ResponseEntity<ErrorResponse> response = handler.handlePagoInvalido(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("BAD_REQUEST", response.getBody().getError());
    }

    @Test
    void errorValidacion_DebeRetornar400() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldCorreo = new FieldError("personaRequest", "correo", "debe ser una direccion de correo electronico valida");
        FieldError fieldNombre = new FieldError("personaRequest", "nombre", "no debe estar vacio");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldCorreo, fieldNombre));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationError(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("VALIDATION_ERROR", body.getError());
        assertNotNull(body.getMessage());
        assertTrue(body.getMessage().contains("correo"));
        assertTrue(body.getMessage().contains("nombre"));
    }

    @Test
    void reservaInvalida_DebeRetornar400() {
        ReservaInvalidaException ex = new ReservaInvalidaException("La hora de fin debe ser posterior a la hora de inicio");

        ResponseEntity<ErrorResponse> response = handler.handleReservaInvalida(ex, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("hora"));
    }

    @Test
    void participanteDuplicado_DebeRetornar409() {
        ParticipanteDuplicadoException ex = new ParticipanteDuplicadoException(7L, 20L);

        ResponseEntity<ErrorResponse> response = handler.handleParticipanteDuplicado(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("CONFLICT", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("7"));
        assertTrue(response.getBody().getMessage().contains("20"));
    }

    @Test
    void pagoNoPermitido_DebeRetornar409() {
        PagoNoPermitidoException ex = new PagoNoPermitidoException("No se permite pagar una reserva cancelada");

        ResponseEntity<ErrorResponse> response = handler.handlePagoNoPermitido(ex, mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("CONFLICT", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("cancelada"));
    }

    @Test
    void ingresoNoPermitido_DebeRetornar403() {
        IngresoNoPermitidoException ex = new IngresoNoPermitidoException("El ingreso solo esta permitido durante el horario de la reserva");

        ResponseEntity<ErrorResponse> response = handler.handleIngresoNoPermitido(ex, mockRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("FORBIDDEN", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("horario"));
    }

    @Test
    void excepcionGenerica_DebeRetornar500SinExponerMensaje() {
        String mensajeSecreto = "ERROR_INTERNO_SECRETO";
        Exception ex = new RuntimeException(mensajeSecreto);

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("Ocurrio un error interno en el servidor", response.getBody().getMessage());
        assertFalse(response.getBody().getMessage().contains(mensajeSecreto));
    }
}
