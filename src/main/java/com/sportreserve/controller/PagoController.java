package com.sportreserve.controller;

import com.sportreserve.dto.request.PagoRequest;
import com.sportreserve.dto.response.PagoResponse;
import com.sportreserve.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reservas/{reservaId}/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> registrarPago(
            @PathVariable Long reservaId,
            @Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.registrarPago(reservaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagoResponse> consultarPago(@PathVariable Long reservaId) {
        PagoResponse response = pagoService.consultarPagoReserva(reservaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable Long reservaId) {
        BigDecimal saldo = pagoService.calcularSaldoPendiente(reservaId);
        return ResponseEntity.ok(saldo);
    }
}
