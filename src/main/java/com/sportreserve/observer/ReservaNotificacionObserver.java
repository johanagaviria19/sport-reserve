package com.sportreserve.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReservaNotificacionObserver implements ReservaObserver {

    private String ultimaNotificacion;

    @EventListener
    public void manejarEvento(ReservaEvent event) {
        actualizar(event);
    }

    @Override
    public void actualizar(ReservaEvent event) {
        if (event == null) {
            this.ultimaNotificacion = null;
            return;
        }
        this.ultimaNotificacion = String.format("Reserva %d actualizada a %s",
                event.getReservaId(), event.getEstado());
    }

    public String getUltimaNotificacion() {
        return ultimaNotificacion;
    }
}
