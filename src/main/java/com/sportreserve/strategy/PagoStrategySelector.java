package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PagoStrategySelector {

    private final Map<String, PagoStrategy> estrategias;

    public PagoStrategySelector(List<PagoStrategy> estrategias) {
        this.estrategias = new HashMap<>();
        for (PagoStrategy estrategia : estrategias) {
            this.estrategias.put(estrategia.getIdentificador(), estrategia);
        }
    }

    public PagoStrategy seleccionar(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new PagoInvalidoException("El método de pago no puede ser nulo o vacío");
        }
        PagoStrategy estrategia = estrategias.get(metodoPago);
        if (estrategia == null) {
            throw new PagoInvalidoException(String.format("Método de pago no soportado: %s", metodoPago));
        }
        return estrategia;
    }
}
