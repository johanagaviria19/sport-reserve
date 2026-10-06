package com.sportreserve.controller;

import com.sportreserve.dto.request.ReservaRequest;
import com.sportreserve.dto.response.ReservaResponse;
import com.sportreserve.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        return new ResponseEntity<>(reservaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> porId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listarTodos());
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @PutMapping("/{id}/en-uso")
    public ResponseEntity<ReservaResponse> marcarEnUso(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.marcarEnUso(id));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ReservaResponse> finalizarReserva(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.finalizar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
