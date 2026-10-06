package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PagoTransferencia implements PagoStrategy {

    public static final String IDENTIFICADOR = "TRANSFERENCIA";

    @Override
    public String getIdentificador() {
        return IDENTIFICADOR;
    }

    @Override
    public String procesar(BigDecimal valor) {
        if (valor == null) {
            throw new PagoInvalidoException("El valor pagado no puede ser nulo (Transferencia)");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoInvalidoException("El valor pagado debe ser mayor a cero (Transferencia)");
        }
        return String.format("CONFIRMACION_TRANSFERENCIA_%s", valor.toPlainString());
    }
}
