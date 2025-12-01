package com.tlapaleria.backend.model;

import java.math.BigDecimal;

public class ProductoDTO {
    private Long id;
    private String clave;
    private String descripcion;
    private String codigo_barras;
    private BigDecimal costo;
    private BigDecimal precio;
    private BigDecimal precioIndividual;
    private Boolean activo;

    private Boolean esProductoPaquete;
    private Integer piezasPorPaquete;
    private Integer piezasIndividuales;

    public ProductoDTO(Long id, String clave, String descripcion, String codigo_barras,
                       BigDecimal costo, BigDecimal precio, BigDecimal precioIndividual,
                       Boolean activo, Boolean esProductoPaquete, Integer piezasPorPaquete,
                       Integer piezasIndividuales) {
        this.id = id;
        this.clave = clave;
        this.descripcion = descripcion;
        this.codigo_barras = codigo_barras;
        this.costo = costo;
        this.precio = precio;
        this.precioIndividual = precioIndividual;
        this.activo = activo;
        this.esProductoPaquete = esProductoPaquete;
        this.piezasPorPaquete = piezasPorPaquete;
        this.piezasIndividuales = piezasIndividuales;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getPrecioIndividual() { return precioIndividual; }
    public void setPrecioIndividual(BigDecimal precioIndividual) { this.precioIndividual = precioIndividual; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getEsProductoPaquete() { return esProductoPaquete; }
    public void setEsProductoPaquete(Boolean esProductoPaquete) { this.esProductoPaquete = esProductoPaquete; }

    public Integer getPiezasPorPaquete() { return piezasPorPaquete; }
    public void setPiezasPorPaquete(Integer piezasPorPaquete) { this.piezasPorPaquete = piezasPorPaquete; }

    public Integer getPiezasIndividuales() { return piezasIndividuales; }
    public void setPiezasIndividuales(Integer piezasIndividuales) { this.piezasIndividuales = piezasIndividuales; }
}