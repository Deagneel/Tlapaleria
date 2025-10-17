package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetalleVenta;
import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.model.Venta;
import com.tlapaleria.backend.repository.DetalleVentaRepository;
import com.tlapaleria.backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping
    public List<DetalleVenta> getAllDetalleVentas() {
        return detalleVentaRepository.findAll();
    }

    @PostMapping
    public DetalleVenta crearDetalleVenta(@RequestBody DetalleVenta detalleVenta) {
        // Asignar precioIndividual si no se envía precio
        if (detalleVenta.getPrecio() == null) {
            detalleVenta.setPrecio(detalleVenta.getProducto().getPrecioIndividual());
        }

        validarDetalle(detalleVenta);

        DetalleVenta nuevo = detalleVentaRepository.save(detalleVenta);
        actualizarTotalVenta(nuevo.getVenta().getId());

        return nuevo;
    }

    @GetMapping("/{id}")
    public DetalleVenta getDetalleVentaById(@PathVariable Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public DetalleVenta actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVentaDetalles) {
        // Asignar precioIndividual si no se envía precio
        if (detalleVentaDetalles.getPrecio() == null) {
            detalleVentaDetalles.setPrecio(detalleVentaDetalles.getProducto().getPrecioIndividual());
        }

        return detalleVentaRepository.findById(id).map(detalle -> {
            validarDetalle(detalleVentaDetalles);

            detalle.setCantidad(detalleVentaDetalles.getCantidad());
            detalle.setPrecio(detalleVentaDetalles.getPrecio());
            detalle.setProducto(detalleVentaDetalles.getProducto());
            detalle.setVenta(detalleVentaDetalles.getVenta());

            DetalleVenta actualizado = detalleVentaRepository.save(detalle);
            actualizarTotalVenta(actualizado.getVenta().getId());

            return actualizado;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de venta no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarDetalleVenta(@PathVariable Long id) {
        DetalleVenta detalle = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));

        Long ventaId = detalle.getVenta().getId();
        detalleVentaRepository.deleteById(id);
        actualizarTotalVenta(ventaId);
    }

    private void validarDetalle(DetalleVenta detalleVenta) {
        if (detalleVenta.getCantidad() == null || detalleVenta.getCantidad() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");

        if (detalleVenta.getPrecio() == null || detalleVenta.getPrecio() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");

        Producto producto = detalleVenta.getProducto();
        if (producto == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no puede ser nulo");

        if (producto.getExistencia() < detalleVenta.getCantidad())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para el producto: " + producto.getDescripcion());
    }

    private void actualizarTotalVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        double nuevoTotal = venta.getDetalles()
                .stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();

        venta.setTotal(nuevoTotal);

        if (venta.getPago_con() < venta.getTotal()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pago debe ser igual o mayor al total de la venta");
        }

        venta.setCambio(venta.getPago_con() - venta.getTotal());
        ventaRepository.save(venta);
    }
}
