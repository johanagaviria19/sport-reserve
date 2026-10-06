package com.sportreserve.controller;

import com.sportreserve.dto.request.PersonaRequest;
import com.sportreserve.dto.response.PersonaResponse;
import com.sportreserve.service.PersonaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping
    public ResponseEntity<PersonaResponse> crear(@Valid @RequestBody PersonaRequest request) {
        return new ResponseEntity<>(personaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponse> porId(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PersonaResponse>> listar() {
        return ResponseEntity.ok(personaService.listarTodos());
    }

    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<PersonaResponse> getByIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(personaService.buscarPorIdentificacion(identificacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.ok(personaService.actualizar(id, request));
    }
}
