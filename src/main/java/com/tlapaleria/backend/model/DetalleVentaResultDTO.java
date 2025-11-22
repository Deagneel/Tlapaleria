package com.tlapaleria.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class DetalleVentaResultDTO {

    private Long id;

    @JsonProperty("producto_id")
    private Long productoId;
    @JsonProperty("precio_individual")
    private BigDecimal precioIndividual;
    private String clave;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;

    private Integer existencia;

    @JsonProperty("existencia_min")
    private Integer existenciaMin;

    public DetalleVentaResultDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }

    public Integer getExistenciaMin() { return existenciaMin; }
    public void setExistenciaMin(Integer existenciaMin) { this.existenciaMin = existenciaMin; }

    public BigDecimal getPrecioIndividual() {
        return precioIndividual;
    }

    public void setPrecioIndividual(BigDecimal precioIndividual) {
        this.precioIndividual = precioIndividual;
    }
}
