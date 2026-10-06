package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagoEfectivoTest {

    private final PagoEfectivo estrategia = new PagoEfectivo();

    @Test
    void getIdentificador_RetornaEfectivo() {
        assertEquals("EFECTIVO", estrategia.getIdentificador());
    }

    @Test
    void procesar_ValorValido_RetornaConfirmacionEfectivo() {
        String resultado = estrategia.procesar(new BigDecimal("50000"));

        assertNotNull(resultado);
        assertTrue(resultado.contains("CONFIRMACION_EFECTIVO"));
        assertTrue(resultado.contains("50000"));
    }

    @Test
    void procesar_ValorNulo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(null));

        assertTrue(exception.getMessage().contains("Efectivo"));
    }

    @Test
    void procesar_ValorCero_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(BigDecimal.ZERO));

        assertTrue(exception.getMessage().contains("Efectivo"));
    }

    @Test
    void procesar_ValorNegativo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(new BigDecimal("-1000")));

        assertTrue(exception.getMessage().contains("Efectivo"));
    }
}
