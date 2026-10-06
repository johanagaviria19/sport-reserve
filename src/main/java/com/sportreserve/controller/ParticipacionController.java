package com.sportreserve.controller;

import com.sportreserve.dto.request.ParticipanteRequest;
import com.sportreserve.dto.response.ParticipanteResponse;
import com.sportreserve.service.ParticipacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas/{reservaId}/participantes")
public class ParticipacionController {

    private final ParticipacionService participacionService;

    public ParticipacionController(ParticipacionService participacionService) {
        this.participacionService = participacionService;
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> agregar(@PathVariable Long reservaId,
                                                        @Valid @RequestBody ParticipanteRequest request) {
        return new ResponseEntity<>(participacionService.agregarParticipante(reservaId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteResponse>> listar(@PathVariable Long reservaId) {
        return ResponseEntity.ok(participacionService.listarParticipantes(reservaId));
    }

    @GetMapping("/{personaId}")
    public ResponseEntity<ParticipanteResponse> buscarParticipante(@PathVariable Long reservaId,
                                                                   @PathVariable Long personaId) {
        return ResponseEntity.ok(participacionService.buscarParticipante(reservaId, personaId));
    }

    @DeleteMapping("/{personaId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long reservaId,
                                         @PathVariable Long personaId) {
        participacionService.eliminarParticipante(reservaId, personaId);
        return ResponseEntity.noContent().build();
    }
}
