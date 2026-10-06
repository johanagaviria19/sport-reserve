package com.sportreserve.controller;

import com.sportreserve.dto.request.ReservaRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.dto.response.ReservaResponse;
import com.sportreserve.enums.EstadoPago;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    ReservaService reservaService;

    ReservaController reservaController;

    @BeforeEach
    void setUp() {
        reservaController = new ReservaController(reservaService);
    }

    @Test
    void crearReserva_PostExitoso_Retorna201() {
        ReservaRequest request = new ReservaRequest();
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(12, 0));
        request.setEscenarioId(1L);
        request.setResponsableId(2L);
        request.setRolResponsable(RolParticipacion.JUGADOR);

        PagoResponse pagoResponse = new PagoResponse();
        pagoResponse.setId(1L);
        pagoResponse.setValorTotal(new BigDecimal("120000"));
        pagoResponse.setValorPagado(BigDecimal.ZERO);
        pagoResponse.setSaldoPendiente(new BigDecimal("120000"));
        pagoResponse.setEstado(EstadoPago.PENDIENTE);
        pagoResponse.setMetodoPago(null);

        ReservaResponse response = new ReservaResponse();
        response.setId(10L);
        response.setEstado(EstadoReserva.PENDIENTE);
        response.setEscenarioId(1L);
        response.setResponsableId(2L);
        response.setRolResponsable(RolParticipacion.JUGADOR);
        response.setPago(pagoResponse);

        when(reservaService.crear(any(ReservaRequest.class))).thenReturn(response);

        ResponseEntity<ReservaResponse> r = reservaController.crear(request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(10L, r.getBody().getId());
        assertEquals(EstadoReserva.PENDIENTE, r.getBody().getEstado());
        assertEquals(0, r.getBody().getPago().getValorTotal().compareTo(new BigDecimal("120000")));
    }

    @Test
    void buscarReserva_GetPorId_Retorna200() {
        ReservaResponse response = new ReservaResponse();
        response.setId(10L);
        response.setEstado(EstadoReserva.CONFIRMADA);
        response.setEscenarioId(1L);

        when(reservaService.buscarPorId(10L)).thenReturn(response);

        ResponseEntity<ReservaResponse> r = reservaController.porId(10L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(10L, r.getBody().getId());
        assertEquals(EstadoReserva.CONFIRMADA, r.getBody().getEstado());
    }

    @Test
    void cancelarReserva_DeleteExitoso_Retorna204() {
        ReservaResponse cancelada = new ReservaResponse();
        cancelada.setId(5L);
        cancelada.setEstado(EstadoReserva.CANCELADA);
        when(reservaService.cancelar(5L)).thenReturn(cancelada);

        ResponseEntity<Void> r = reservaController.cancelar(5L);

        assertEquals(HttpStatus.NO_CONTENT, r.getStatusCode());
        verify(reservaService).cancelar(5L);
    }
}
