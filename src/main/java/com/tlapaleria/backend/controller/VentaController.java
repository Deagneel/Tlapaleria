package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.Venta;
import com.tlapaleria.backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping
    public List<Venta> getVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio;
            LocalDateTime fin;
            try {
                inicio = LocalDateTime.parse(fechaInicio);
                fin = LocalDateTime.parse(fechaFin);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Use yyyy-MM-ddTHH:mm:ss");
            }
            return ventaRepository.findByFechaBetween(inicio, fin);
        }
        return ventaRepository.findAll();
    }

    @PostMapping
    public Venta crearVenta(@RequestBody Venta venta) {
        if (venta.getDetalles() != null) {
            double total = venta.getDetalles().stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                    .sum();
            venta.setTotal(total);

            if (venta.getPago_con() != null) {
                venta.setCambio(venta.getPago_con() - total);
            }
        }
        return ventaRepository.save(venta);
    }

    @GetMapping("/{id}")
    public Venta getVentaById(@PathVariable Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable Long id, @RequestBody Venta detallesVenta) {
        return ventaRepository.findById(id).map(venta -> {
            venta.setDetalles(detallesVenta.getDetalles());
            venta.setPago_con(detallesVenta.getPago_con());

            if (venta.getDetalles() != null) {
                double total = venta.getDetalles().stream()
                        .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                        .sum();
                venta.setTotal(total);

                if (venta.getPago_con() != null) {
                    venta.setCambio(venta.getPago_con() - total);
                }
            }
            return ventaRepository.save(venta);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventaRepository.deleteById(id);
    }
}
