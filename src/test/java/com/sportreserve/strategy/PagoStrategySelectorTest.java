package com.sportreserve.strategy;

import com.sportreserve.exception.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagoStrategySelectorTest {

    private final PagoStrategySelector selector = new PagoStrategySelector(List.of(
            new PagoEfectivo(),
            new PagoNequi(),
            new PagoTarjeta(),
            new PagoTransferencia()
    ));

    @Test
    void seleccionar_CuandoEfectivo_RetornaPagoEfectivo() {
        PagoStrategy resultado = selector.seleccionar("EFECTIVO");

        assertNotNull(resultado);
        assertInstanceOf(PagoEfectivo.class, resultado);
    }

    @Test
    void seleccionar_CuandoNequi_RetornaPagoNequi() {
        PagoStrategy resultado = selector.seleccionar("NEQUI");

        assertNotNull(resultado);
        assertInstanceOf(PagoNequi.class, resultado);
    }

    @Test
    void seleccionar_CuandoTarjeta_RetornaPagoTarjeta() {
        PagoStrategy resultado = selector.seleccionar("TARJETA");

        assertNotNull(resultado);
        assertInstanceOf(PagoTarjeta.class, resultado);
    }

    @Test
    void seleccionar_CuandoTransferencia_RetornaPagoTransferencia() {
        PagoStrategy resultado = selector.seleccionar("TRANSFERENCIA");

        assertNotNull(resultado);
        assertInstanceOf(PagoTransferencia.class, resultado);
    }

    @Test
    void seleccionar_CuandoMetodoNulo_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> selector.seleccionar(null));

        assertTrue(exception.getMessage().contains("no puede ser nulo o vacío"));
    }

    @Test
    void seleccionar_CuandoMetodoVacio_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> selector.seleccionar(""));

        assertTrue(exception.getMessage().contains("no puede ser nulo o vacío"));
    }

    @Test
    void seleccionar_CuandoMetodoNoSoportado_LanzaPagoInvalidoException() {
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
                () -> selector.seleccionar("CRIPTO"));

        assertTrue(exception.getMessage().contains("Método de pago no soportado"));
        assertTrue(exception.getMessage().contains("CRIPTO"));
    }
}
