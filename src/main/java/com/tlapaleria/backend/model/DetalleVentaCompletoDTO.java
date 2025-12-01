package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleVentaCompletoDTO {
    private Long id;
    private String clave;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precio;
    private LocalDateTime fechaRegistro;

    public DetalleVentaCompletoDTO(Long id, String clave, String descripcion,
                                   Integer cantidad, BigDecimal precio, LocalDateTime fechaRegistro) {
        this.id = id;
        this.clave = clave;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public String getClave() { return clave; }
    public String getDescripcion() { return descripcion; }
    public Integer getCantidad() { return cantidad; }
    public BigDecimal getPrecio() { return precio; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
