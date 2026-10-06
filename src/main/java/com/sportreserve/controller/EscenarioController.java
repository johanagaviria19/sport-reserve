package com.sportreserve.controller;

import com.sportreserve.dto.request.EscenarioRequest;
import com.sportreserve.dto.response.EscenarioResponse;
import com.sportreserve.service.EscenarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/escenarios")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @PostMapping
    public ResponseEntity<EscenarioResponse> crear(@Valid @RequestBody EscenarioRequest request) {
        return new ResponseEntity<>(escenarioService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscenarioResponse> porId(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<EscenarioResponse>> listar() {
        return ResponseEntity.ok(escenarioService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscenarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody EscenarioRequest request) {
        return ResponseEntity.ok(escenarioService.actualizar(id, request));
    }
}
