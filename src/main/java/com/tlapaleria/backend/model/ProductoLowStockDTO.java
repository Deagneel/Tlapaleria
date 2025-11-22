package com.tlapaleria.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductoLowStockDTO {
    @JsonProperty("producto_id")
    private Long productoId;
    private String clave;
    private String descripcion;
    private Integer existencia;
    @JsonProperty("existencia_min")
    private Integer existenciaMin;

    public ProductoLowStockDTO() {}

    public ProductoLowStockDTO(Long productoId, String clave, String descripcion, Integer existencia, Integer existenciaMin) {
        this.productoId = productoId;
        this.clave = clave;
        this.descripcion = descripcion;
        this.existencia = existencia;
        this.existenciaMin = existenciaMin;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }

    public Integer getExistenciaMin() { return existenciaMin; }
    public void setExistenciaMin(Integer existenciaMin) { this.existenciaMin = existenciaMin; }
}
