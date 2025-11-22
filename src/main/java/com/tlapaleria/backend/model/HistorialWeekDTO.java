package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistorialWeekDTO {
    private int year;
    private int week;
    private LocalDate startDate;
    private LocalDate endDate;
    private int ventasCount;
    private BigDecimal totalSemana;

    public HistorialWeekDTO() {}

    public HistorialWeekDTO(int year, int week, LocalDate startDate, LocalDate endDate, int ventasCount, BigDecimal totalSemana) {
        this.year = year;
        this.week = week;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ventasCount = ventasCount;
        this.totalSemana = totalSemana;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getVentasCount() { return ventasCount; }
    public void setVentasCount(int ventasCount) { this.ventasCount = ventasCount; }

    public BigDecimal getTotalSemana() { return totalSemana; }
    public void setTotalSemana(BigDecimal totalSemana) { this.totalSemana = totalSemana; }
}
