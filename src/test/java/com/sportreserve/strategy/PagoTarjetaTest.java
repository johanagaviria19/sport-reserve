package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagoTarjetaTest {

    private final PagoTarjeta estrategia = new PagoTarjeta();

    @Test
    void getIdentificador_RetornaTarjeta() {
        assertEquals("TARJETA", estrategia.getIdentificador());
    }

    @Test
    void procesar_ValorValido_RetornaConfirmacionTarjeta() {
        String resultado = estrategia.procesar(new BigDecimal("100000"));

        assertNotNull(resultado);
        assertTrue(resultado.contains("CONFIRMACION_TARJETA"));
        assertTrue(resultado.contains("100000"));
    }

    @Test
    void procesar_ValorNulo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(null));

        assertTrue(exception.getMessage().contains("Tarjeta"));
    }

    @Test
    void procesar_ValorCero_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(BigDecimal.ZERO));

        assertTrue(exception.getMessage().contains("Tarjeta"));
    }

    @Test
    void procesar_ValorNegativo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(new BigDecimal("-9999")));

        assertTrue(exception.getMessage().contains("Tarjeta"));
    }
}
