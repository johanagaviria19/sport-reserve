package com.sportreserve.controller;

import com.sportreserve.dto.request.EscenarioRequest;
import com.sportreserve.dto.response.EscenarioResponse;
import com.sportreserve.enums.TipoEscenario;
import com.sportreserve.service.EscenarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscenarioControllerTest {

    @Mock
    private EscenarioService escenarioService;

    private EscenarioController escenarioController;

    @BeforeEach
    void setUp() {
        escenarioController = new EscenarioController(escenarioService);
    }

    @Test
    void crearEscenario_PostExitoso_Retorna201() {
        EscenarioRequest request = new EscenarioRequest();
        request.setNombre("Cancha 1");
        request.setPrecioPorHora(new BigDecimal("50000"));
        request.setCapacidadJugadores(10);
        request.setTipo(TipoEscenario.FUTBOL);

        EscenarioResponse responseEsperado = new EscenarioResponse();
        responseEsperado.setId(1L);
        responseEsperado.setNombre("Cancha 1");
        responseEsperado.setPrecioPorHora(new BigDecimal("50000"));
        responseEsperado.setCapacidadJugadores(10);
        responseEsperado.setTipo(TipoEscenario.FUTBOL);

        when(escenarioService.crear(any(EscenarioRequest.class))).thenReturn(responseEsperado);

        ResponseEntity<EscenarioResponse> r = escenarioController.crear(request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getId());
        assertEquals("Cancha 1", r.getBody().getNombre());
        assertEquals(TipoEscenario.FUTBOL, r.getBody().getTipo());
    }

    @Test
    void buscarEscenario_GetPorId_Retorna200() {
        EscenarioResponse responseEsperado = new EscenarioResponse();
        responseEsperado.setId(5L);
        responseEsperado.setNombre("Micro 2");
        responseEsperado.setPrecioPorHora(new BigDecimal("40000"));
        responseEsperado.setCapacidadJugadores(8);
        responseEsperado.setTipo(TipoEscenario.MICROFUTBOL);

        when(escenarioService.buscarPorId(5L)).thenReturn(responseEsperado);

        ResponseEntity<EscenarioResponse> r = escenarioController.porId(5L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(5L, r.getBody().getId());
        assertEquals("Micro 2", r.getBody().getNombre());
    }
}
