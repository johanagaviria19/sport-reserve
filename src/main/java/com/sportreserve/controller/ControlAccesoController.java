package com.sportreserve.controller;

import com.sportreserve.dto.request.IngresoRequest;
import com.sportreserve.dto.response.IngresoResponse;
import com.sportreserve.service.ControlAccesoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas/{reservaId}/ingresos")
public class ControlAccesoController {

    private final ControlAccesoService controlAccesoService;

    public ControlAccesoController(ControlAccesoService controlAccesoService) {
        this.controlAccesoService = controlAccesoService;
    }

    @PostMapping
    public ResponseEntity<IngresoResponse> registrarIngreso(
            @PathVariable Long reservaId,
            @Valid @RequestBody IngresoRequest request) {
        IngresoResponse response = controlAccesoService.registrarIngreso(reservaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
