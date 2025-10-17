package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetalleVenta;
import com.tlapaleria.backend.model.Venta;
import com.tlapaleria.backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

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

    @GetMapping("/resumen")
    public List<Map<String, Object>> resumenVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        List<Venta> ventas;

        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio;
            LocalDateTime fin;
            try {
                inicio = LocalDateTime.parse(fechaInicio);
                fin = LocalDateTime.parse(fechaFin);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Use yyyy-MM-ddTHH:mm:ss");
            }
            ventas = ventaRepository.findByFechaBetween(inicio, fin);
        } else {
            ventas = ventaRepository.findAll();
        }

        return ventas.stream().map(venta -> {
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("id", venta.getId());
            resumen.put("fecha", venta.getFecha());
            resumen.put("total", venta.getTotal());
            resumen.put("pago_con", venta.getPago_con());
            resumen.put("cambio", venta.getCambio());

            // Usamos getDetalles() directamente
            resumen.put("detalles", venta.getDetalles().stream().map(dv -> Map.of(
                    "producto", dv.getProducto().getDescripcion(),
                    "cantidad", dv.getCantidad(),
                    "precio", dv.getPrecio(),
                    "subtotal", dv.getSubtotal()
            )).toList());

            return resumen;
        }).toList();
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
    @GetMapping("/{id}/ticket")
    public Map<String, Object> generarTicket(@PathVariable Long id) {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);

        if (ventaOpt.isEmpty()) {
            throw new RuntimeException("Venta no encontrada con id: " + id);
        }

        Venta venta = ventaOpt.get();

        // Construimos el mapa que representará el ticket
        Map<String, Object> ticket = new LinkedHashMap<>();
        ticket.put("id", venta.getId());
        ticket.put("fecha", venta.getFecha());
        ticket.put("total", venta.getTotal());
        ticket.put("pago_con", venta.getPago_con());
        ticket.put("cambio", venta.getCambio());

        // Construimos la lista de detalles
        List<Map<String, Object>> detalles = new ArrayList<>();
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Map<String, Object> det = new LinkedHashMap<>();
                det.put("producto", detalle.getProducto().getDescripcion());
                det.put("cantidad", detalle.getCantidad());
                det.put("precio", detalle.getPrecio());
                det.put("subtotal", detalle.getSubtotal());
                detalles.add(det);
            }
        }

        ticket.put("detalles", detalles);

        return ticket;
    }

}
