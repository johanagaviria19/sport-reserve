package com.sportreserve.service;

import com.sportreserve.dto.request.EscenarioRequest;
import com.sportreserve.dto.response.EscenarioResponse;
import com.sportreserve.exception.RecursoNoEncontradoException;
import com.sportreserve.exception.ReservaInvalidaException;
import com.sportreserve.factory.EscenarioFactory;
import com.sportreserve.model.Escenario;
import com.sportreserve.repository.EscenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EscenarioService {

    private final EscenarioRepository escenarioRepository;
    private final EscenarioFactory escenarioFactory;

    public EscenarioService(EscenarioRepository escenarioRepository, EscenarioFactory escenarioFactory) {
        this.escenarioRepository = escenarioRepository;
        this.escenarioFactory = escenarioFactory;
    }

    @Transactional
    public EscenarioResponse crear(EscenarioRequest request) {
        if (request.getPrecioPorHora() == null || request.getPrecioPorHora().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ReservaInvalidaException("El precio por hora debe ser positivo");
        }
        if (request.getCapacidadJugadores() == null || request.getCapacidadJugadores() <= 0) {
            throw new ReservaInvalidaException("La capacidad de jugadores debe ser positiva");
        }

        Escenario escenario = escenarioFactory.crear(request.getTipo());
        escenario.setNombre(request.getNombre());
        escenario.setPrecioPorHora(request.getPrecioPorHora());
        escenario.setCapacidadJugadores(request.getCapacidadJugadores());

        Escenario guardado = escenarioRepository.save(escenario);
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public EscenarioResponse buscarPorId(Long id) {
        Escenario escenario = obtenerEntidadPorId(id);
        return toResponse(escenario);
    }

    @Transactional(readOnly = true)
    public Escenario obtenerEntidadPorId(Long id) {
        return escenarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Escenario", id));
    }

    @Transactional(readOnly = true)
    public List<EscenarioResponse> listarTodos() {
        return escenarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EscenarioResponse actualizar(Long id, EscenarioRequest request) {
        Escenario escenario = obtenerEntidadPorId(id);

        if (request.getPrecioPorHora() == null || request.getPrecioPorHora().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ReservaInvalidaException("El precio por hora debe ser positivo");
        }
        if (request.getCapacidadJugadores() == null || request.getCapacidadJugadores() <= 0) {
            throw new ReservaInvalidaException("La capacidad de jugadores debe ser positiva");
        }

        escenario.setNombre(request.getNombre());
        escenario.setPrecioPorHora(request.getPrecioPorHora());
        escenario.setCapacidadJugadores(request.getCapacidadJugadores());

        Escenario guardado = escenarioRepository.save(escenario);
        return toResponse(guardado);
    }

    private EscenarioResponse toResponse(Escenario escenario) {
        EscenarioResponse response = new EscenarioResponse();
        response.setId(escenario.getId());
        response.setNombre(escenario.getNombre());
        response.setPrecioPorHora(escenario.getPrecioPorHora());
        response.setCapacidadJugadores(escenario.getCapacidadJugadores());
        response.setTipo(escenario.getTipo());
        return response;
    }
}
