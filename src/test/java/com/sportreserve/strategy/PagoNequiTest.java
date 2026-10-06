package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagoNequiTest {

    private final PagoNequi estrategia = new PagoNequi();

    @Test
    void getIdentificador_RetornaNequi() {
        assertEquals("NEQUI", estrategia.getIdentificador());
    }

    @Test
    void procesar_ValorValido_RetornaConfirmacionNequi() {
        String resultado = estrategia.procesar(new BigDecimal("30000"));

        assertNotNull(resultado);
        assertTrue(resultado.contains("CONFIRMACION_NEQUI"));
        assertTrue(resultado.contains("30000"));
    }

    @Test
    void procesar_ValorNulo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(null));

        assertTrue(exception.getMessage().contains("Nequi"));
    }

    @Test
    void procesar_ValorCero_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(BigDecimal.ZERO));

        assertTrue(exception.getMessage().contains("Nequi"));
    }

    @Test
    void procesar_ValorNegativo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(new BigDecimal("-500")));

        assertTrue(exception.getMessage().contains("Nequi"));
    }
}
