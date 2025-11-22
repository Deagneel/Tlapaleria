package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistorialMonthDTO {
    private int year;
    private int month; // 1..12
    private LocalDate startDate;
    private LocalDate endDate;
    private int ventasCount;
    private BigDecimal totalMes;

    public HistorialMonthDTO() {}

    public HistorialMonthDTO(int year, int month, LocalDate startDate, LocalDate endDate, int ventasCount, BigDecimal totalMes) {
        this.year = year;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ventasCount = ventasCount;
        this.totalMes = totalMes;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getVentasCount() { return ventasCount; }
    public void setVentasCount(int ventasCount) { this.ventasCount = ventasCount; }

    public BigDecimal getTotalMes() { return totalMes; }
    public void setTotalMes(BigDecimal totalMes) { this.totalMes = totalMes; }
}
