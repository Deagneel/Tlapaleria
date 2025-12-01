package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.repository.ProductoRepository;
import com.tlapaleria.backend.repository.DetallePedidoRepository;
import com.tlapaleria.backend.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        if (producto.getEsProductoPaquete() == null) {
            producto.setEsProductoPaquete(false);
        }
        if (producto.getPiezasPorPaquete() == null) {
            producto.setPiezasPorPaquete(1);
        }

        if (producto.getPiezasIndividuales() == null) {
            producto.setPiezasIndividuales(0);
        }

        validarProducto(producto);
        return productoRepository.save(producto);
    }

    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto detallesProducto) {
        return productoRepository.findById(id).map(producto -> {
            producto.setClave(detallesProducto.getClave());
            producto.setDescripcion(detallesProducto.getDescripcion());
            producto.setCodigo_barras(detallesProducto.getCodigo_barras());
            producto.setCosto(detallesProducto.getCosto());
            producto.setPrecio(detallesProducto.getPrecio());
            producto.setPrecioIndividual(detallesProducto.getPrecioIndividual());
            producto.setExistencia(detallesProducto.getExistencia());
            producto.setExistencia_min(detallesProducto.getExistencia_min());
            producto.setUnidad(detallesProducto.getUnidad());
            producto.setActivo(detallesProducto.getActivo());
            producto.setEsProductoPaquete(detallesProducto.getEsProductoPaquete());
            producto.setPiezasPorPaquete(detallesProducto.getPiezasPorPaquete());
            producto.setPiezasIndividuales(detallesProducto.getPiezasIndividuales());

            validarProducto(producto);
            return productoRepository.save(producto);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        boolean tienePedidos = detallePedidoRepository.existsByProductoId(id);
        boolean tieneVentas = detalleVentaRepository.existsByProductoId(id);

        if (tienePedidos || tieneVentas) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar el producto porque está asociado a pedidos o ventas.");
        }

        productoRepository.delete(producto);
    }

    private void validarProducto(Producto producto) {
        if (producto.getClave() == null || producto.getClave().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave del producto es obligatoria");

        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción es obligatoria");

        if (producto.getCosto() == null || producto.getCosto().compareTo(BigDecimal.ZERO) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El costo no puede ser negativo");

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio por caja no puede ser negativo");

        if (producto.getPrecioIndividual() == null || producto.getPrecioIndividual().compareTo(BigDecimal.ZERO) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio individual no puede ser negativo");

        if (producto.getExistencia() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La existencia es obligatoria");

        if (producto.getExistencia_min() == null || producto.getExistencia_min() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La existencia mínima no puede ser negativa");

        if (producto.getPiezasPorPaquete() != null && producto.getPiezasPorPaquete() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las piezas por paquete deben ser al menos 1");
        }

    }
}