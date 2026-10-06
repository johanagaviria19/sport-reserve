package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PagoNequi implements PagoStrategy {

    public static final String IDENTIFICADOR = "NEQUI";

    @Override
    public String getIdentificador() {
        return IDENTIFICADOR;
    }

    @Override
    public String procesar(BigDecimal valor) {
        if (valor == null) {
            throw new PagoInvalidoException("El valor pagado no puede ser nulo (Nequi)");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoInvalidoException("El valor pagado debe ser mayor a cero (Nequi)");
        }
        return String.format("CONFIRMACION_NEQUI_%s", valor.toPlainString());
    }
}
