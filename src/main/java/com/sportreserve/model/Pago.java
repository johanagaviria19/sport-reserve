package com.sportreserve.model;

import com.sportreserve.enums.EstadoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El valor total es obligatorio")
    @Positive(message = "El valor total debe ser positivo")
    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @NotNull(message = "El valor pagado es obligatorio")
    @PositiveOrZero(message = "El valor pagado no puede ser negativo")
    @Column(name = "valor_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPagado;

    @NotNull(message = "El saldo pendiente es obligatorio")
    @PositiveOrZero(message = "El saldo pendiente no puede ser negativo")
    @Column(name = "saldo_pendiente", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoPendiente;

    @NotNull(message = "El estado de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPago estado;

    @Column(name = "metodo_pago", length = 30)
    private String metodoPago;

    public Pago() {
    }

    public Pago(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
        this.valorPagado = BigDecimal.ZERO;
        this.saldoPendiente = valorTotal;
        this.estado = EstadoPago.PENDIENTE;
        this.metodoPago = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorPagado() {
        return valorPagado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void aplicarPago(BigDecimal monto, String metodoPagoIdentificador) {
        BigDecimal nuevoPagado = this.valorPagado.add(monto);
        this.valorPagado = nuevoPagado;
        this.saldoPendiente = this.valorTotal.subtract(this.valorPagado);
        this.metodoPago = metodoPagoIdentificador;
        if (this.saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            this.estado = EstadoPago.PAGADO;
        } else {
            this.estado = EstadoPago.PAGADO_PARCIAL;
        }
    }

    public boolean esPagoCompleto() {
        return this.estado == EstadoPago.PAGADO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago pago)) return false;
        return id != null && Objects.equals(id, pago.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : System.identityHashCode(this));
    }

    @Override
    public String toString() {
        return "Pago{id=" + id + ", valorTotal=" + valorTotal + ", valorPagado=" + valorPagado + ", saldoPendiente=" + saldoPendiente + ", estado=" + estado + ", metodoPago='" + metodoPago + "'}";
    }
}
