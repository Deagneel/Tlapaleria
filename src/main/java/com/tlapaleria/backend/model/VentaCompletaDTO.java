package com.tlapaleria.backend.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public class VentaCompletaDTO {
    private Long id;
    private BigDecimal total;
    private LocalDateTime fecha;
    private List<DetalleVentaCompletoDTO> detalles;

    public VentaCompletaDTO(Long id, BigDecimal total, LocalDateTime fecha, List<DetalleVentaCompletoDTO> detalles) {
        this.id = id;
        this.total = total;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public Long getId() { return id; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleVentaCompletoDTO> getDetalles() { return detalles; }
}
