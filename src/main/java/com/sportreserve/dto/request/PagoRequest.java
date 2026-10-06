package com.sportreserve.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PagoRequest {

    @NotNull(message = "El valor pagado es obligatorio")
    @Positive(message = "El valor pagado debe ser positivo")
    private BigDecimal valorPagado;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private String metodoPago;

    public BigDecimal getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(BigDecimal valorPagado) {
        this.valorPagado = valorPagado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
