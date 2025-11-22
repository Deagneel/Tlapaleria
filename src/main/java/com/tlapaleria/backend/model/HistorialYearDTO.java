package com.tlapaleria.backend.model;


import java.math.BigDecimal;
import java.time.LocalDate;


public class HistorialYearDTO {
    private int year;
    private LocalDate startDate;
    private LocalDate endDate;
    private int ventasCount;
    private BigDecimal totalYear;


    public HistorialYearDTO() {}


    public HistorialYearDTO(int year, LocalDate startDate, LocalDate endDate, int ventasCount, BigDecimal totalYear) {
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ventasCount = ventasCount;
        this.totalYear = totalYear;
    }


    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }


    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }


    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }


    public int getVentasCount() { return ventasCount; }
    public void setVentasCount(int ventasCount) { this.ventasCount = ventasCount; }


    public BigDecimal getTotalYear() { return totalYear; }
    public void setTotalYear(BigDecimal totalYear) { this.totalYear = totalYear; }
}