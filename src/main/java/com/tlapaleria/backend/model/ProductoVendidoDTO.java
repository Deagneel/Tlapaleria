package com.tlapaleria.backend.model;

import java.math.BigDecimal;

public class ProductoVendidoDTO {
    private Long productoId;
    private String clave;
    private String descripcion;
    private Integer cantidadTotal;
    private BigDecimal subtotalTotal;
    private Integer veces; // cuántas ventas lo incluyeron
    private Integer existencia;
    private Integer existenciaMin;

    private Boolean esProductoPaquete;
    private Integer piezasPorPaquete;


    public ProductoVendidoDTO() {}

    public ProductoVendidoDTO(Long productoId, String clave, String descripcion,
                              Integer cantidadTotal, BigDecimal subtotalTotal,
                              Integer veces, Integer existencia, Integer existenciaMin,
                              Boolean esProductoPaquete, Integer piezasPorPaquete) {
        this.productoId = productoId;
        this.clave = clave;
        this.descripcion = descripcion;
        this.cantidadTotal = cantidadTotal;
        this.subtotalTotal = subtotalTotal;
        this.veces = veces;
        this.existencia = existencia;
        this.existenciaMin = existenciaMin;
        this.esProductoPaquete = esProductoPaquete;
        this.piezasPorPaquete = piezasPorPaquete;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(Integer cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public BigDecimal getSubtotalTotal() { return subtotalTotal; }
    public void setSubtotalTotal(BigDecimal subtotalTotal) { this.subtotalTotal = subtotalTotal; }

    public Integer getVeces() { return veces; }
    public void setVeces(Integer veces) { this.veces = veces; }

    public Integer getExistencia() { return existencia; }
    public Integer getExistenciaMin() { return existenciaMin; }

    public Boolean getEsProductoPaquete() { return esProductoPaquete; }
    public void setEsProductoPaquete(Boolean esProductoPaquete) { this.esProductoPaquete = esProductoPaquete; }
    public Integer getPiezasPorPaquete() { return piezasPorPaquete; }
    public void setPiezasPorPaquete(Integer piezasPorPaquete) { this.piezasPorPaquete = piezasPorPaquete; }
}
