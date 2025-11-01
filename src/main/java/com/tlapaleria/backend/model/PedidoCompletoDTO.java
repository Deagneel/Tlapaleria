package com.tlapaleria.backend.model;

import java.math.BigDecimal;
import java.util.List;

public class PedidoCompletoDTO {

    private Long id;
    private String cliente;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoDTO> detalles;

    public static class DetallePedidoDTO {
        private Long id;
        private Long producto_id;
        private Integer cantidad;
        private BigDecimal precio;
        private Boolean recibido;

        // Nuevo campo
        private ProductoDTO producto;

        // Getters y setters existentes
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProducto_id() { return producto_id; }
        public void setProducto_id(Long producto_id) { this.producto_id = producto_id; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }

        public Boolean getRecibido() { return recibido; }
        public void setRecibido(Boolean recibido) { this.recibido = recibido; }

        // Getter y setter para el producto completo
        public ProductoDTO getProducto() { return producto; }
        public void setProducto(ProductoDTO producto) { this.producto = producto; }
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetallePedidoDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoDTO> detalles) { this.detalles = detalles; }
}
