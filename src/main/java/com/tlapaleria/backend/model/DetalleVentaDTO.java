package com.tlapaleria.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class DetalleVentaDTO {

    @JsonProperty("producto_id")
    private Long productoId;

    private Integer cantidad;

    @JsonProperty("precio_individual")
    private BigDecimal precioIndividual;

    private BigDecimal precio;

    public DetalleVentaDTO() {}

    public DetalleVentaDTO(Long productoId, Integer cantidad, BigDecimal precio) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioIndividual() {
        return precioIndividual;
    }

    public void setPrecioIndividual(BigDecimal precioIndividual) {
        this.precioIndividual = precioIndividual;
    }
}
