package com.sportreserve.controller;

import com.sportreserve.dto.request.ParticipanteRequest;
import com.sportreserve.dto.response.ParticipanteResponse;
import com.sportreserve.enums.EstadoIngreso;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.service.ParticipacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipacionControllerTest {

    @Mock
    ParticipacionService participacionService;

    ParticipacionController participacionController;

    @BeforeEach
    void setUp() {
        participacionController = new ParticipacionController(participacionService);
    }

    @Test
    void agregarParticipante_PostExitoso_Retorna201() {
        ParticipanteRequest request = new ParticipanteRequest();
        request.setPersonaId(5L);
        request.setRol(RolParticipacion.JUGADOR);

        ParticipanteResponse response = new ParticipanteResponse();
        response.setPersonaId(5L);
        response.setNombreCompleto("Carlos Gomez");
        response.setIdentificacion("12345");
        response.setRol(RolParticipacion.JUGADOR);
        response.setEstadoIngreso(EstadoIngreso.REGISTRADO);
        response.setFechaHoraIngreso(null);

        when(participacionService.agregarParticipante(eq(1L), any(ParticipanteRequest.class))).thenReturn(response);

        ResponseEntity<ParticipanteResponse> r = participacionController.agregar(1L, request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(5L, r.getBody().getPersonaId());
        assertEquals(RolParticipacion.JUGADOR, r.getBody().getRol());
        assertEquals(EstadoIngreso.REGISTRADO, r.getBody().getEstadoIngreso());
    }

    @Test
    void listarParticipantes_Get_Retorna200() {
        ParticipanteResponse p1 = new ParticipanteResponse();
        p1.setPersonaId(5L);
        p1.setNombreCompleto("Carlos Gomez");
        p1.setIdentificacion("123");
        p1.setRol(RolParticipacion.JUGADOR);
        p1.setEstadoIngreso(EstadoIngreso.REGISTRADO);
        p1.setFechaHoraIngreso(null);

        ParticipanteResponse p2 = new ParticipanteResponse();
        p2.setPersonaId(6L);
        p2.setNombreCompleto("Ana Diaz");
        p2.setIdentificacion("456");
        p2.setRol(RolParticipacion.ESPECTADOR);
        p2.setEstadoIngreso(EstadoIngreso.REGISTRADO);
        p2.setFechaHoraIngreso(null);

        when(participacionService.listarParticipantes(3L)).thenReturn(List.of(p1, p2));

        ResponseEntity<List<ParticipanteResponse>> r = participacionController.listar(3L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(2, r.getBody().size());
        assertEquals(RolParticipacion.JUGADOR, r.getBody().get(0).getRol());
        assertEquals(RolParticipacion.ESPECTADOR, r.getBody().get(1).getRol());
    }
}
