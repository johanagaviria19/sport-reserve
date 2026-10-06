package com.sportreserve.controller;

import com.sportreserve.dto.request.PagoRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.enums.EstadoPago;
import com.sportreserve.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    PagoService pagoService;

    PagoController pagoController;

    @BeforeEach
    void setUp() {
        pagoController = new PagoController(pagoService);
    }

    @Test
    void registrarPago_PostExitoso_Retorna201() {
        PagoRequest request = new PagoRequest();
        request.setValorPagado(new BigDecimal("50000"));
        request.setMetodoPago("EFECTIVO");

        PagoResponse mockResponse = new PagoResponse();
        mockResponse.setId(1L);
        mockResponse.setValorTotal(new BigDecimal("120000"));
        mockResponse.setValorPagado(new BigDecimal("50000"));
        mockResponse.setSaldoPendiente(new BigDecimal("70000"));
        mockResponse.setEstado(EstadoPago.PAGADO_PARCIAL);
        mockResponse.setMetodoPago("EFECTIVO");

        when(pagoService.registrarPago(eq(1L), any(PagoRequest.class))).thenReturn(mockResponse);

        ResponseEntity<PagoResponse> r = pagoController.registrarPago(1L, request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        PagoResponse body = r.getBody();
        assertEquals(0, body.getValorPagado().compareTo(new BigDecimal("50000")));
        assertEquals(0, body.getSaldoPendiente().compareTo(new BigDecimal("70000")));
        assertSame(EstadoPago.PAGADO_PARCIAL, body.getEstado());
        assertEquals("EFECTIVO", body.getMetodoPago());
    }

    @Test
    void consultarPago_Get_Retorna200() {
        PagoResponse mockResponse = new PagoResponse();
        mockResponse.setId(1L);
        mockResponse.setValorTotal(new BigDecimal("120000"));
        mockResponse.setValorPagado(BigDecimal.ZERO);
        mockResponse.setSaldoPendiente(new BigDecimal("120000"));
        mockResponse.setEstado(EstadoPago.PENDIENTE);
        mockResponse.setMetodoPago(null);

        when(pagoService.consultarPagoReserva(1L)).thenReturn(mockResponse);

        ResponseEntity<PagoResponse> r = pagoController.consultarPago(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        PagoResponse body = r.getBody();
        assertEquals(0, body.getValorTotal().compareTo(new BigDecimal("120000")));
        assertSame(EstadoPago.PENDIENTE, body.getEstado());
    }
}
