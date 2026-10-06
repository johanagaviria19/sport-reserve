package com.sportreserve.service;

import com.sportreserve.dto.request.EscenarioRequest;
import com.sportreserve.dto.response.EscenarioResponse;
import com.sportreserve.enums.TipoEscenario;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.factory.EscenarioFactory;
import com.sportreserve.model.CanchaFutbol;
import com.sportreserve.repository.EscenarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscenarioServiceTest {

    @Mock
    private EscenarioRepository escenarioRepository;

    @Mock
    private EscenarioFactory escenarioFactory;

    @InjectMocks
    private EscenarioService escenarioService;

    @Test
    void crearEscenario_CuandoDatosValidos_DevuelveEscenarioResponse() {
        EscenarioRequest request = new EscenarioRequest();
        request.setNombre("Cancha 1");
        request.setPrecioPorHora(new BigDecimal("50000"));
        request.setCapacidadJugadores(10);
        request.setTipo(TipoEscenario.FUTBOL);

        CanchaFutbol canchaCreada = new CanchaFutbol(null, BigDecimal.ZERO, 0);
        CanchaFutbol canchaGuardada = new CanchaFutbol("Cancha 1", new BigDecimal("50000"), 10);
        canchaGuardada.setId(1L);

        when(escenarioFactory.crear(TipoEscenario.FUTBOL)).thenReturn(canchaCreada);
        when(escenarioRepository.save(any(CanchaFutbol.class))).thenReturn(canchaGuardada);

        EscenarioResponse response = escenarioService.crear(request);

        assertEquals(1L, response.getId());
        assertEquals("Cancha 1", response.getNombre());
        assertEquals(10, response.getCapacidadJugadores());
        assertEquals(TipoEscenario.FUTBOL, response.getTipo());
        assertEquals(new BigDecimal("50000"), response.getPrecioPorHora());
        verify(escenarioRepository, times(1)).save(any(CanchaFutbol.class));
    }

    @Test
    void crearEscenario_CuandoDatosValidos_InvocaFactoryCrear() {
        EscenarioRequest request = new EscenarioRequest();
        request.setNombre("Cancha Test");
        request.setPrecioPorHora(new BigDecimal("80000"));
        request.setCapacidadJugadores(12);
        request.setTipo(TipoEscenario.MICROFUTBOL);

        CanchaFutbol mockEscenario = new CanchaFutbol(null, BigDecimal.ZERO, 0);
        when(escenarioFactory.crear(TipoEscenario.MICROFUTBOL)).thenReturn(mockEscenario);
        when(escenarioRepository.save(any())).thenReturn(mockEscenario);

        escenarioService.crear(request);

        verify(escenarioFactory, times(1)).crear(TipoEscenario.MICROFUTBOL);
    }

    @Test
    void buscarPorId_CuandoNoExiste_LanzaRecursoNoEncontrado() {
        when(escenarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException excepcion = assertThrows(RecursoNoEncontradoException.class,
                () -> escenarioService.buscarPorId(99L));
        assertTrue(excepcion.getMessage().contains("Escenario"));
    }
}
