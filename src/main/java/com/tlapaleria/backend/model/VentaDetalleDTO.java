package com.tlapaleria.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDetalleDTO {

    private Long id;
    private BigDecimal total;

    @JsonProperty("pago_con")
    private BigDecimal pagoCon;

    private BigDecimal cambio;

    @JsonProperty("cargo_extra")
    private BigDecimal cargoExtra;

    private LocalDateTime fecha;

    private List<DetalleVentaResultDTO> detalles;

    public VentaDetalleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getPagoCon() { return pagoCon; }
    public void setPagoCon(BigDecimal pagoCon) { this.pagoCon = pagoCon; }

    public BigDecimal getCambio() { return cambio; }
    public void setCambio(BigDecimal cambio) { this.cambio = cambio; }

    public BigDecimal getCargoExtra() { return cargoExtra; }
    public void setCargoExtra(BigDecimal cargoExtra) { this.cargoExtra = cargoExtra; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public List<DetalleVentaResultDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaResultDTO> detalles) { this.detalles = detalles; }
}
