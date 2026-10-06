package com.sportreserve.service;

import com.sportreserve.dto.request.PersonaRequest;
import com.sportreserve.dto.response.PersonaResponse;
import com.sportreserve.exception.IdentificacionDuplicadaException;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.model.Persona;
import com.sportreserve.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Transactional
    public PersonaResponse crear(PersonaRequest request) {
        if (personaRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new IdentificacionDuplicadaException(request.getIdentificacion());
        }

        Persona persona = new Persona(
                request.getIdentificacion(),
                request.getNombre(),
                request.getApellido(),
                request.getTelefono(),
                request.getCorreo()
        );

        Persona personaGuardada = personaRepository.save(persona);
        return convertirAResponse(personaGuardada);
    }

    @Transactional(readOnly = true)
    public PersonaResponse buscarPorId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", id));
        return convertirAResponse(persona);
    }

    @Transactional(readOnly = true)
    public PersonaResponse buscarPorIdentificacion(String identificacion) {
        Persona persona = personaRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        String.format("Persona no encontrada con identificacion: %s", identificacion)
                ));
        return convertirAResponse(persona);
    }

    @Transactional(readOnly = true)
    public List<PersonaResponse> listarTodos() {
        return personaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PersonaResponse actualizar(Long id, PersonaRequest request) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", id));

        String identificacionNueva = request.getIdentificacion();
        if (!persona.getIdentificacion().equals(identificacionNueva)
                && personaRepository.existsByIdentificacion(identificacionNueva)) {
            throw new IdentificacionDuplicadaException(identificacionNueva);
        }

        persona.setIdentificacion(identificacionNueva);
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setTelefono(request.getTelefono());
        persona.setCorreo(request.getCorreo());

        Persona personaActualizada = personaRepository.save(persona);
        return convertirAResponse(personaActualizada);
    }

    private PersonaResponse convertirAResponse(Persona persona) {
        PersonaResponse response = new PersonaResponse();
        response.setId(persona.getId());
        response.setIdentificacion(persona.getIdentificacion());
        response.setNombre(persona.getNombre());
        response.setApellido(persona.getApellido());
        response.setTelefono(persona.getTelefono());
        response.setCorreo(persona.getCorreo());
        return response;
    }
}
