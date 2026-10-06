package com.sportreserve.factory;

import com.sportreserve.enums.TipoEscenario;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.model.CanchaBaloncesto;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.CanchaMicrofutbol;
import com.sportreserve.model.CanchaVoleibol;
import com.sportreserve.model.Escenario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EscenarioFactoryTest {

    private final EscenarioFactory factory = new EscenarioFactory();

    @Test
    void crear_CuandoTipoFutbol_DevuelveCanchaFutbol() {
        Escenario resultado = factory.crear(TipoEscenario.FUTBOL);

        assertNotNull(resultado);
        assertInstanceOf(CanchaFutbol.class, resultado);
        assertEquals(TipoEscenario.FUTBOL, resultado.getTipo());
    }

    @Test
    void crear_CuandoTipoMicrofutbol_DevuelveCanchaMicrofutbol() {
        Escenario resultado = factory.crear(TipoEscenario.MICROFUTBOL);

        assertNotNull(resultado);
        assertInstanceOf(CanchaMicrofutbol.class, resultado);
        assertEquals(TipoEscenario.MICROFUTBOL, resultado.getTipo());
    }

    @Test
    void crear_CuandoTipoBaloncesto_DevuelveCanchaBaloncesto() {
        Escenario resultado = factory.crear(TipoEscenario.BALONCESTO);

        assertNotNull(resultado);
        assertInstanceOf(CanchaBaloncesto.class, resultado);
        assertEquals(TipoEscenario.BALONCESTO, resultado.getTipo());
    }

    @Test
    void crear_CuandoTipoVoleibol_DevuelveCanchaVoleibol() {
        Escenario resultado = factory.crear(TipoEscenario.VOLEIBOL);

        assertNotNull(resultado);
        assertInstanceOf(CanchaVoleibol.class, resultado);
        assertEquals(TipoEscenario.VOLEIBOL, resultado.getTipo());
    }

    @Test
    void crear_CuandoTipoNull_LanzaReservaInvalidaException() {
        ReservaInvalidaException excepcion = assertThrows(ReservaInvalidaException.class,
                () -> factory.crear(null));

        assertTrue(excepcion.getMessage().contains("tipo de escenario no puede ser nulo"));
    }
}
