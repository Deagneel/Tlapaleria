package com.tlapaleria.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class VentaDTO {

    private List<DetalleVentaDTO> detalles;

    @JsonProperty("cargo_extra")
    private BigDecimal cargoExtra;

    @JsonProperty("pago_con")
    private BigDecimal pagoCon;

    private BigDecimal total;

    public VentaDTO() {}

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getCargoExtra() {
        return cargoExtra;
    }

    public void setCargoExtra(BigDecimal cargoExtra) {
        this.cargoExtra = cargoExtra;
    }

    public BigDecimal getPagoCon() {
        return pagoCon;
    }

    public void setPagoCon(BigDecimal pagoCon) {
        this.pagoCon = pagoCon;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
