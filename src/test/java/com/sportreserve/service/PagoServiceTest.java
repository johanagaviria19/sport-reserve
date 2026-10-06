package com.sportreserve.service;

import com.sportreserve.dto.request.PagoRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.enums.EstadoPago;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.exception.PagoInvalidoException;
import com.sportreserve.exception.PagoNoPermitidoException;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.Pago;
import com.sportreserve.model.Persona;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.ReservaRepository;
import com.sportreserve.strategy.PagoStrategy;
import com.sportreserve.strategy.PagoEfectivo;
import com.sportreserve.strategy.PagoStrategySelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PagoStrategySelector pagoStrategySelector;

    @InjectMocks
    private PagoService pagoService;

    private Reserva crearReservaBase(Long id, EstadoReserva estado, BigDecimal valorTotalPago) {
        Persona responsable = new Persona("123456789", "Juan", "Perez", "3001234567", "juan@correo.com");
        responsable.setId(10L);

        CanchaFutbol cancha = new CanchaFutbol("Cancha 1", new BigDecimal("60000"), 22);
        cancha.setId(5L);

        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado(estado);
        reserva.setResponsable(responsable);
        reserva.setEscenario(cancha);
        reserva.setPago(new Pago(valorTotalPago));
        return reserva;
    }

    private PagoRequest crearPagoRequest(BigDecimal valorPagado, String metodoPago) {
        PagoRequest request = new PagoRequest();
        request.setValorPagado(valorPagado);
        request.setMetodoPago(metodoPago);
        return request;
    }

    @Test
    void registrarPago_PagoParcial_ActualizaValores() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE, new BigDecimal("120000"));
        PagoStrategy mockStrategy = mock(PagoEfectivo.class);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagoStrategySelector.seleccionar("EFECTIVO")).thenReturn(mockStrategy);
        when(mockStrategy.procesar(any(BigDecimal.class))).thenReturn("CONFIRMACION_EFECTIVO_50000");
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        PagoRequest request = crearPagoRequest(new BigDecimal("50000"), "EFECTIVO");

        PagoResponse response = pagoService.registrarPago(1L, request);

        assertEquals(new BigDecimal("50000"), response.getValorPagado());
        assertEquals(new BigDecimal("70000"), response.getSaldoPendiente());
        assertEquals(EstadoPago.PAGADO_PARCIAL, response.getEstado());
        assertEquals("EFECTIVO", response.getMetodoPago());
    }

    @Test
    void registrarPago_PagoCompleto_EstadoPagado() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE, new BigDecimal("120000"));
        PagoStrategy mockStrategy = mock(PagoStrategy.class);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagoStrategySelector.seleccionar("TARJETA")).thenReturn(mockStrategy);
        when(mockStrategy.procesar(any(BigDecimal.class))).thenReturn("CONFIRMACION_TARJETA_120000");
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        PagoRequest request = crearPagoRequest(new BigDecimal("120000"), "TARJETA");

        PagoResponse response = pagoService.registrarPago(1L, request);

        assertEquals(new BigDecimal("0"), response.getSaldoPendiente());
        assertEquals(EstadoPago.PAGADO, response.getEstado());
    }

    @Test
    void registrarPago_UtilizaStrategyCorrespondiente_InvocaSelectorYProcesar() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE, new BigDecimal("80000"));
        PagoStrategy mockStrategy = mock(PagoStrategy.class);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagoStrategySelector.seleccionar("NEQUI")).thenReturn(mockStrategy);
        when(mockStrategy.procesar(new BigDecimal("30000"))).thenReturn("CONFIRMACION_NEQUI_30000");
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        PagoRequest request = crearPagoRequest(new BigDecimal("30000"), "NEQUI");
        pagoService.registrarPago(1L, request);

        verify(pagoStrategySelector, times(1)).seleccionar("NEQUI");
        verify(mockStrategy, times(1)).procesar(new BigDecimal("30000"));
    }

    @Test
    void registrarPago_PagoSuperaSaldo_LanzaPagoInvalido() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE, new BigDecimal("120000"));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(new BigDecimal("150000"), "EFECTIVO");

        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrarPago(1L, request));
        assertTrue(exception.getMessage().contains("supera el saldo"));
    }

    @Test
    void registrarPago_ValorCeroOLess_LanzaPagoInvalido() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.PENDIENTE, new BigDecimal("120000"));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        PagoRequest requestCero = crearPagoRequest(BigDecimal.ZERO, "EFECTIVO");
        PagoInvalidoException exceptionCero = assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrarPago(1L, requestCero));
        assertTrue(exceptionCero.getMessage().contains("mayor a cero"));

        PagoRequest requestNegativo = crearPagoRequest(new BigDecimal("-10000"), "EFECTIVO");
        PagoInvalidoException exceptionNegativo = assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrarPago(1L, requestNegativo));
        assertTrue(exceptionNegativo.getMessage().contains("mayor a cero"));
    }

    @Test
    void registrarPago_ReservaCancelada_LanzaPagoNoPermitido() {
        Reserva reserva = crearReservaBase(1L, EstadoReserva.CANCELADA, new BigDecimal("120000"));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(new BigDecimal("50000"), "EFECTIVO");

        assertThrows(PagoNoPermitidoException.class,
                () -> pagoService.registrarPago(1L, request));
    }
}
