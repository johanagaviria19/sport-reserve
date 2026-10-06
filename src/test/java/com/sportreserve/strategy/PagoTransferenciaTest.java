package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagoTransferenciaTest {

    private final PagoTransferencia estrategia = new PagoTransferencia();

    @Test
    void getIdentificador_RetornaTransferencia() {
        assertEquals("TRANSFERENCIA", estrategia.getIdentificador());
    }

    @Test
    void procesar_ValorValido_RetornaConfirmacionTransferencia() {
        String resultado = estrategia.procesar(new BigDecimal("200000"));

        assertNotNull(resultado);
        assertTrue(resultado.contains("CONFIRMACION_TRANSFERENCIA"));
        assertTrue(resultado.contains("200000"));
    }

    @Test
    void procesar_ValorNulo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(null));

        assertTrue(exception.getMessage().contains("Transferencia"));
    }

    @Test
    void procesar_ValorCero_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(BigDecimal.ZERO));

        assertTrue(exception.getMessage().contains("Transferencia"));
    }

    @Test
    void procesar_ValorNegativo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> estrategia.procesar(new BigDecimal("-77777")));

        assertTrue(exception.getMessage().contains("Transferencia"));
    }
}
