package com.tlapaleria.backend.model;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private Double costo;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Double precioIndividual;

    private Integer existencia;
    private Integer existencia_min;
    private String unidad;
    private Boolean activo;

    // Getters y Setters
    public Double getPrecioIndividual() { return precioIndividual; }
    public void setPrecioIndividual(Double precioIndividual) { this.precioIndividual = precioIndividual; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }

    public Integer getExistencia_min() { return existencia_min; }
    public void setExistencia_min(Integer existencia_min) { this.existencia_min = existencia_min; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
