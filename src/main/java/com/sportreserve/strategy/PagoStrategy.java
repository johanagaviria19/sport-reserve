package com.sportreserve.strategy;

import java.math.BigDecimal;

public interface PagoStrategy {

    String getIdentificador();

    String procesar(BigDecimal valor);
}
