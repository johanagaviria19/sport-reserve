package com.sportreserve.service;

import com.sportreserve.dto.request.PersonaRequest;
import com.sportreserve.dto.response.PersonaResponse;
import com.sportreserve.exception.IdentificacionDuplicadaException;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.model.Persona;
import com.sportreserve.repository.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService personaService;

    @Test
    void crearPersona_CuandoIdentificacionUnica_DevuelvePersonaResponse() {
        PersonaRequest request = new PersonaRequest();
        request.setIdentificacion("123");
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setTelefono("555");
        request.setCorreo("j@x.com");

        Persona personaGuardada = new Persona("123", "Juan", "Perez", "555", "j@x.com");
        personaGuardada.setId(1L);

        when(personaRepository.existsByIdentificacion("123")).thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);

        PersonaResponse response = personaService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123", response.getIdentificacion());
        assertEquals("Juan", response.getNombre());
        verify(personaRepository, times(1)).save(any(Persona.class));
    }

    @Test
    void crearPersona_CuandoIdentificacionDuplicada_LanzaExcepcion() {
        PersonaRequest request = new PersonaRequest();
        request.setIdentificacion("123");
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setTelefono("555");
        request.setCorreo("j@x.com");

        when(personaRepository.existsByIdentificacion("123")).thenReturn(true);

        assertThrows(IdentificacionDuplicadaException.class, () -> personaService.crear(request));
        verify(personaRepository, never()).save(any(Persona.class));
    }

    @Test
    void buscarPorId_CuandoNoExiste_LanzaRecursoNoEncontrado() {
        when(personaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> personaService.buscarPorId(999L));
    }
}
