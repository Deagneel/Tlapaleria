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

    private Boolean esProductoPaquete;
    private Integer piezasPorPaquete;
    private Integer piezasIndividuales;

    public ProductoLowStockDTO() {}

    public ProductoLowStockDTO(Long productoId, String clave, String descripcion,
                               Integer existencia, Integer existenciaMin,
                               Boolean esProductoPaquete, Integer piezasPorPaquete,
                               Integer piezasIndividuales) {
        this.productoId = productoId;
        this.clave = clave;
        this.descripcion = descripcion;
        this.existencia = existencia;
        this.existenciaMin = existenciaMin;
        this.esProductoPaquete = esProductoPaquete;
        this.piezasPorPaquete = piezasPorPaquete;
        this.piezasIndividuales = piezasIndividuales;
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

    public Boolean getEsProductoPaquete() { return esProductoPaquete; }
    public void setEsProductoPaquete(Boolean esProductoPaquete) { this.esProductoPaquete = esProductoPaquete; }

    public Integer piezasPorPaquete() { return piezasPorPaquete; }
    public void setpiezasPorPaquete(Integer piezasPorPaquete) { this.piezasPorPaquete = piezasPorPaquete; }

    public Integer piezasIndividuales() { return piezasIndividuales; }
    public void setpiezasIndividuales(Integer piezasIndividuales) { this.piezasIndividuales = piezasIndividuales; }
}
