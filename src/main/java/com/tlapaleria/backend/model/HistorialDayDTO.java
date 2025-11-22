package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HistorialDayDTO {
    private LocalDate date;
    private int ventasCount;
    private BigDecimal totalDia;
    private List<VentaResumenHistorialDTO> ventas;

    public HistorialDayDTO() {}

    public HistorialDayDTO(LocalDate date, int ventasCount, BigDecimal totalDia, List<VentaResumenHistorialDTO> ventas) {
        this.date = date;
        this.ventasCount = ventasCount;
        this.totalDia = totalDia;
        this.ventas = ventas;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getVentasCount() { return ventasCount; }
    public void setVentasCount(int ventasCount) { this.ventasCount = ventasCount; }

    public BigDecimal getTotalDia() { return totalDia; }
    public void setTotalDia(BigDecimal totalDia) { this.totalDia = totalDia; }

    public List<VentaResumenHistorialDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaResumenHistorialDTO> ventas) { this.ventas = ventas; }
}
