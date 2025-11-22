package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResumenHistorialDTO {
    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private int lineas; // número de líneas (detalles)

    public VentaResumenHistorialDTO() {}

    public VentaResumenHistorialDTO(Long id, LocalDateTime fecha, BigDecimal total, int lineas) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.lineas = lineas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public int getLineas() { return lineas; }
    public void setLineas(int lineas) { this.lineas = lineas; }
}
