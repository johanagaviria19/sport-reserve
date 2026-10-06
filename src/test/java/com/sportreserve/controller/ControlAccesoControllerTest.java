package com.sportreserve.controller;

import com.sportreserve.dto.request.IngresoRequest;
import com.sportreserve.dto.response.IngresoResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.service.ControlAccesoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControlAccesoControllerTest {

    @Mock
    ControlAccesoService controlAccesoService;

    ControlAccesoController controlAccesoController;

    @BeforeEach
    void setUp() {
        controlAccesoController = new ControlAccesoController(controlAccesoService);
    }

    @Test
    void registrarIngreso_PostExitoso_Retorna201() {
        IngresoRequest request = new IngresoRequest();
        request.setPersonaId(5L);

        LocalDateTime ahora = LocalDateTime.now();

        IngresoResponse mockResponse = new IngresoResponse();
        mockResponse.setPersonaId(5L);
        mockResponse.setNombreCompleto("Carlos Gomez");
        mockResponse.setRol(RolParticipacion.JUGADOR);
        mockResponse.setEstadoIngreso(EstadoIngreso.INGRESO);
        mockResponse.setFechaHoraIngreso(ahora);

        when(controlAccesoService.registrarIngreso(eq(1L), any(IngresoRequest.class))).thenReturn(mockResponse);

        ResponseEntity<IngresoResponse> r = controlAccesoController.registrarIngreso(1L, request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        IngresoResponse body = r.getBody();
        assertEquals(5L, body.getPersonaId());
        assertSame(RolParticipacion.JUGADOR, body.getRol());
        assertSame(EstadoIngreso.INGRESO, body.getEstadoIngreso());
        assertNotNull(body.getFechaHoraIngreso());
        assertTrue(body.getFechaHoraIngreso().isEqual(ahora) ||
                body.getFechaHoraIngreso().isAfter(ahora.minusSeconds(1)) &&
                body.getFechaHoraIngreso().isBefore(ahora.plusSeconds(1)));
    }
}
