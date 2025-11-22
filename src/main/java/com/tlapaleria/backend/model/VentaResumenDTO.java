package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResumenDTO {

    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;

    public VentaResumenDTO(Long id, LocalDateTime fecha, BigDecimal total) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
