package com.tlapaleria.backend.model;

import java.math.BigDecimal;

public class DetallePedidoDTO {
    private Long id;
    private Integer cantidad;
    private BigDecimal precio;
    private Long productoId;
    private String productoClave;
    private String productoDescripcion;

    public DetallePedidoDTO(Long id, Integer cantidad, BigDecimal precio, Long productoId, String productoClave, String productoDescripcion) {
        this.id = id;
        this.cantidad = cantidad;
        this.precio = precio;
        this.productoId = productoId;
        this.productoClave = productoClave;
        this.productoDescripcion = productoDescripcion;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoClave() { return productoClave; }
    public void setProductoClave(String productoClave) { this.productoClave = productoClave; }
    public String getProductoDescripcion() { return productoDescripcion; }
    public void setProductoDescripcion(String productoDescripcion) { this.productoDescripcion = productoDescripcion; }
}
