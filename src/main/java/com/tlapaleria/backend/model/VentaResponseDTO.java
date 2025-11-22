package com.tlapaleria.backend.model;

import java.util.List;

public class VentaResponseDTO {

    private VentaDetalleDTO venta;
    private List<ProductoLowStockDTO> lowStock;

    public VentaResponseDTO() {}

    public VentaResponseDTO(VentaDetalleDTO venta, List<ProductoLowStockDTO> lowStock) {
        this.venta = venta;
        this.lowStock = lowStock;
    }

    public VentaDetalleDTO getVenta() {
        return venta;
    }

    public void setVenta(VentaDetalleDTO venta) {
        this.venta = venta;
    }

    public List<ProductoLowStockDTO> getLowStock() {
        return lowStock;
    }

    public void setLowStock(List<ProductoLowStockDTO> lowStock) {
        this.lowStock = lowStock;
    }
}
