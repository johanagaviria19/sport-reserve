package com.sportreserve.observer;

import com.sportreserve.enums.EstadoReserva;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservaNotificacionObserverTest {

    private final ReservaNotificacionObserver observer = new ReservaNotificacionObserver();

    @Test
    void implementaReservaObserver() {
        assertTrue(ReservaObserver.class.isAssignableFrom(ReservaNotificacionObserver.class));
        assertInstanceOf(ReservaObserver.class, observer);
    }

    @Test
    void actualizar_EventoCreacionPendiente_GeneraNotificacionConIdYEstado() {
        ReservaEvent event = new ReservaEvent(10L, EstadoReserva.PENDIENTE);

        observer.actualizar(event);

        String notificacion = observer.getUltimaNotificacion();
        assertNotNull(notificacion);
        assertTrue(notificacion.contains("10"));
        assertTrue(notificacion.contains("PENDIENTE"));
    }

    @Test
    void manejarEvento_EventoCreacionPendiente_InvocaActualizarYGeneraNotificacion() {
        ReservaEvent event = new ReservaEvent(25L, EstadoReserva.PENDIENTE);

        observer.manejarEvento(event);

        String notificacion = observer.getUltimaNotificacion();
        assertNotNull(notificacion);
        assertTrue(notificacion.contains("25"));
        assertTrue(notificacion.contains("PENDIENTE"));
    }

    @Test
    void actualizar_EventoCancelacion_GeneraNotificacionConIdYCancelada() {
        ReservaEvent event = new ReservaEvent(7L, EstadoReserva.CANCELADA);

        observer.actualizar(event);

        String notificacion = observer.getUltimaNotificacion();
        assertNotNull(notificacion);
        assertTrue(notificacion.contains("7"));
        assertTrue(notificacion.contains("CANCELADA"));
    }

    @Test
    void manejarEvento_EventoCancelacion_InvocaActualizarYGeneraNotificacion() {
        ReservaEvent event = new ReservaEvent(99L, EstadoReserva.CANCELADA);

        observer.manejarEvento(event);

        String notificacion = observer.getUltimaNotificacion();
        assertNotNull(notificacion);
        assertTrue(notificacion.contains("99"));
        assertTrue(notificacion.contains("CANCELADA"));
    }

    @Test
    void getUltimaNotificacion_SinEventos_RetornaNull() {
        assertNull(observer.getUltimaNotificacion());
    }

    @Test
    void actualizar_EventoNulo_RetornaNullUltimaNotificacion() {
        ReservaEvent eventPrevio = new ReservaEvent(1L, EstadoReserva.PENDIENTE);
        observer.actualizar(eventPrevio);
        assertNotNull(observer.getUltimaNotificacion());

        observer.actualizar(null);
        assertNull(observer.getUltimaNotificacion());
    }
}
