package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoResumenDTO {
    private Long id;
    private String cliente;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;

    public PedidoResumenDTO(Long id, String cliente, LocalDateTime fecha, String estado, BigDecimal total) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
