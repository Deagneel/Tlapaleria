package com.tlapaleria.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clave;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, unique = true)
    private String codigo_barras;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(nullable = false, precision = 10, scale = 2)  // precio por caja
    private BigDecimal precio;

    @Column(precision = 19, scale = 2)
    private BigDecimal precioIndividual;

    private Integer existencia;
    private Integer existencia_min;
    private String unidad;
    private Boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    public BigDecimal getCosto() { return costo != null ? costo : BigDecimal.ZERO; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public BigDecimal getPrecio() { return precio != null ? precio : BigDecimal.ZERO; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getPrecioIndividual() { return precioIndividual; }
    public void setPrecioIndividual(BigDecimal precioIndividual) { this.precioIndividual = precioIndividual; }

    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }

    public Integer getExistencia_min() { return existencia_min; }
    public void setExistencia_min(Integer existencia_min) { this.existencia_min = existencia_min; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
