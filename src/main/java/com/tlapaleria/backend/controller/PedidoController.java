package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetallePedido;
import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public List<Pedido> getPedidos(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isEmpty()) {
            EstadoPedido estadoEnum;
            try {
                estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de pedido inválido");
            }
            return pedidoRepository.findByEstado(estadoEnum);
        }
        return pedidoRepository.findAll();
    }


    @GetMapping("/resumen")
    public List<Map<String, Object>> resumenPedidos(@RequestParam(required = false) String estado) {
        List<Pedido> pedidos;
        if (estado != null && !estado.isEmpty()) {
            EstadoPedido estadoEnum;
            try {
                estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de pedido inválido");
            }
            pedidos = pedidoRepository.findByEstado(estadoEnum);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return pedidos.stream().map(pedido -> {
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("id", pedido.getId());
            resumen.put("fecha", pedido.getFecha());
            resumen.put("estado", pedido.getEstado());
            resumen.put("total", pedido.getTotal());

            // Usamos getDetalles() directamente
            resumen.put("detalles", pedido.getDetalles().stream().map(dp -> Map.of(
                    "producto", dp.getProducto().getDescripcion(),
                    "cantidad", dp.getCantidad(),
                    "precio", dp.getPrecio(),
                    "subtotal", dp.getSubtotal()
            )).toList());

            return resumen;
        }).toList();
    }


    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        if (pedido.getDetalles() != null) {
            double total = pedido.getDetalles().stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                    .sum();
            pedido.setTotal(total);
        }
        return pedidoRepository.save(pedido);
    }

    @GetMapping("/{id}")
    public Pedido getPedidoById(@PathVariable Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoDetalles) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(pedidoDetalles.getEstado());
            pedido.setDetalles(pedidoDetalles.getDetalles());
            if (pedido.getDetalles() != null) {
                double total = pedido.getDetalles().stream()
                        .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                        .sum();
                pedido.setTotal(total);
            }
            return pedidoRepository.save(pedido);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }
    @GetMapping("/{id}/resumen")
    public Map<String, Object> generarResumenPedido(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }

        Pedido pedido = pedidoOpt.get();

        // Estructura principal del resumen
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("id", pedido.getId());
        resumen.put("fecha", pedido.getFecha());
        resumen.put("total", pedido.getTotal());

        // Lista simple de productos con id y cantidad
        List<Map<String, Object>> productos = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Map<String, Object> prod = new LinkedHashMap<>();
                prod.put("id_producto", detalle.getProducto().getId());
                prod.put("cantidad", detalle.getCantidad());
                productos.add(prod);
            }
        }

        resumen.put("productos", productos);

        return resumen;
    }

}
