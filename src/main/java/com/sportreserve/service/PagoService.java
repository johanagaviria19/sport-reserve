package com.sportreserve.service;

import com.sportreserve.dto.request.PagoRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.exception.PagoInvalidoException;
import com.sportreserve.exception.PagoNoPermitidoException;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.model.Pago;
import com.sportreserve.model.Reserva;
import com.sportreserve.repository.ReservaRepository;
import com.sportreserve.strategy.PagoStrategy;
import com.sportreserve.strategy.PagoStrategySelector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PagoService {

    private final ReservaRepository reservaRepository;
    private final PagoStrategySelector pagoStrategySelector;

    public PagoService(ReservaRepository reservaRepository, PagoStrategySelector pagoStrategySelector) {
        this.reservaRepository = reservaRepository;
        this.pagoStrategySelector = pagoStrategySelector;
    }

    @Transactional
    public PagoResponse registrarPago(Long reservaId, PagoRequest request) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new PagoNoPermitidoException(reservaId, reserva.getEstado());
        }

        Pago pago = reserva.getPago();
        if (pago == null) {
            throw new PagoInvalidoException("La reserva no tiene un pago asociado");
        }

        if (request.getValorPagado() == null || request.getValorPagado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoInvalidoException("El valor pagado debe ser mayor a cero");
        }
        if (request.getValorPagado().compareTo(pago.getSaldoPendiente()) > 0) {
            throw new PagoInvalidoException(String.format("El valor pagado %s supera el saldo pendiente %s",
                    request.getValorPagado().toPlainString(), pago.getSaldoPendiente().toPlainString()));
        }

        PagoStrategy estrategia = pagoStrategySelector.seleccionar(request.getMetodoPago());
        estrategia.procesar(request.getValorPagado());

        pago.aplicarPago(request.getValorPagado(), request.getMetodoPago());

        reservaRepository.save(reserva);

        return toResponse(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponse consultarPagoReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        Pago pago = reserva.getPago();
        if (pago == null) {
            throw new PagoInvalidoException("La reserva no tiene pago asociado");
        }

        return toResponse(pago);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoPendiente(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));

        Pago pago = reserva.getPago();
        if (pago == null) {
            return BigDecimal.ZERO;
        }

        return pago.getSaldoPendiente();
    }

    private PagoResponse toResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setValorTotal(pago.getValorTotal());
        response.setValorPagado(pago.getValorPagado());
        response.setSaldoPendiente(pago.getSaldoPendiente());
        response.setEstado(pago.getEstado());
        response.setMetodoPago(pago.getMetodoPago());
        return response;
    }
}
