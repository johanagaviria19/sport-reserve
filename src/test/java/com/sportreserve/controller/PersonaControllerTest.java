package com.sportreserve.controller;

import com.sportreserve.dto.request.PersonaRequest;
import com.sportreserve.dto.response.PersonaResponse;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.service.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaControllerTest {

    @Mock
    private PersonaService personaService;

    private PersonaController personaController;

    @BeforeEach
    void setUp() {
        personaController = new PersonaController(personaService);
    }

    @Test
    void crearPersona_PostExitoso_Retorna201() {
        PersonaRequest request = new PersonaRequest();
        request.setIdentificacion("123");
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setTelefono("555");
        request.setCorreo("j@x.com");

        PersonaResponse responseEsperado = new PersonaResponse();
        responseEsperado.setId(1L);
        responseEsperado.setIdentificacion("123");
        responseEsperado.setNombre("Juan");
        responseEsperado.setApellido("Perez");
        responseEsperado.setTelefono("555");
        responseEsperado.setCorreo("j@x.com");

        when(personaService.crear(any(PersonaRequest.class))).thenReturn(responseEsperado);

        ResponseEntity<PersonaResponse> resp = personaController.crear(request);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(1L, resp.getBody().getId());
        assertEquals("Juan", resp.getBody().getNombre());
        verify(personaService).crear(any(PersonaRequest.class));
    }

    @Test
    void buscarPersona_GetPorId_Retorna200() {
        PersonaResponse responseEsperado = new PersonaResponse();
        responseEsperado.setId(1L);
        responseEsperado.setIdentificacion("123");
        responseEsperado.setNombre("Ana");
        responseEsperado.setApellido("M");
        responseEsperado.setTelefono("999");
        responseEsperado.setCorreo("a@x.com");

        when(personaService.buscarPorId(1L)).thenReturn(responseEsperado);

        ResponseEntity<PersonaResponse> r = personaController.porId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getId());
        assertEquals("Ana", r.getBody().getNombre());
    }

    @Test
    void actualizarPersona_PutExitoso_Retorna200() {
        PersonaRequest request = new PersonaRequest();
        request.setIdentificacion("123");
        request.setNombre("Juan Actualizado");
        request.setApellido("Perez");
        request.setTelefono("555");
        request.setCorreo("j@x.com");

        PersonaResponse responseEsperado = new PersonaResponse();
        responseEsperado.setId(1L);
        responseEsperado.setIdentificacion("123");
        responseEsperado.setNombre("Juan Actualizado");
        responseEsperado.setApellido("Perez");
        responseEsperado.setTelefono("555");
        responseEsperado.setCorreo("j@x.com");

        when(personaService.actualizar(eq(1L), any(PersonaRequest.class))).thenReturn(responseEsperado);

        ResponseEntity<PersonaResponse> r = personaController.actualizar(1L, request);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getId());
        assertEquals("Juan Actualizado", r.getBody().getNombre());
    }

    @Test
    void buscarPersona_NoExiste_PropagaExcepcion_HandlerManejara() {
        when(personaService.buscarPorId(999L)).thenThrow(new RecursoNoEncontradoException("Persona", 999L));

        assertThrows(RecursoNoEncontradoException.class, () -> personaController.porId(999L));
        verify(personaService).buscarPorId(999L);
    }
}
