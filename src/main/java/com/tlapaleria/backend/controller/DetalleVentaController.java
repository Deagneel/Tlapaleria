package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetalleVenta;
import com.tlapaleria.backend.repository.DetalleVentaRepository;
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

    @GetMapping
    public List<DetalleVenta> getAllDetalleVentas() {
        return detalleVentaRepository.findAll();
    }

    @PostMapping
    public DetalleVenta crearDetalleVenta(@RequestBody DetalleVenta detalleVenta) {
        if(detalleVenta.getCantidad() == null || detalleVenta.getCantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }

        if(detalleVenta.getPrecio() == null || detalleVenta.getPrecio() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
        }
        return detalleVentaRepository.save(detalleVenta);
    }

    @GetMapping("/{id}")
    public DetalleVenta getDetalleVentaById(@PathVariable Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public DetalleVenta actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVentaDetalles) {
        return detalleVentaRepository.findById(id).map(detalle -> {
            if(detalleVentaDetalles.getCantidad() == null || detalleVentaDetalles.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
            }

            if(detalleVentaDetalles.getPrecio() == null || detalleVentaDetalles.getPrecio() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
            }
            detalle.setCantidad(detalleVentaDetalles.getCantidad());
            detalle.setPrecio(detalleVentaDetalles.getPrecio());
            detalle.setProducto(detalleVentaDetalles.getProducto());
            detalle.setVenta(detalleVentaDetalles.getVenta());
            return detalleVentaRepository.save(detalle);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarDetalleVenta(@PathVariable Long id) {
        detalleVentaRepository.deleteById(id);
    }
}
