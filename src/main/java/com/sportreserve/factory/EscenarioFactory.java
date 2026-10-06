package com.sportreserve.factory;

import com.sportreserve.enums.TipoEscenario;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.model.CanchaBaloncesto;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.model.CanchaMicrofutbol;
import com.sportreserve.model.CanchaVoleibol;
import com.sportreserve.model.Escenario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EscenarioFactory {

    public Escenario crear(TipoEscenario tipo) {
        if (tipo == null) {
            throw new ReservaInvalidaException("El tipo de escenario no puede ser nulo");
        }
        return switch (tipo) {
            case FUTBOL -> new CanchaFutbol(null, BigDecimal.ZERO, 0);
            case MICROFUTBOL -> new CanchaMicrofutbol(null, BigDecimal.ZERO, 0);
            case BALONCESTO -> new CanchaBaloncesto(null, BigDecimal.ZERO, 0);
            case VOLEIBOL -> new CanchaVoleibol(null, BigDecimal.ZERO, 0);
        };
    }
}
