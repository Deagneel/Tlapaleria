package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetallePedido;
import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @GetMapping("/filtrar")
    public List<Pedido> filtrarPedidos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        EstadoPedido estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de pedido inválido");
            }
        }

        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        try {
            if (fechaInicio != null && !fechaInicio.isEmpty()) inicio = LocalDateTime.parse(fechaInicio);
            if (fechaFin != null && !fechaFin.isEmpty()) fin = LocalDateTime.parse(fechaFin);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido");
        }

        List<Pedido> pedidos = pedidoRepository.findAll();

        if (estadoEnum != null) pedidos = pedidoRepository.findByEstado(estadoEnum);

        if (inicio != null && fin != null) {
            LocalDateTime finalInicio = inicio;
            LocalDateTime finalFin = fin;
            pedidos = pedidos.stream()
                    .filter(p -> !p.getFecha().isBefore(finalInicio) && !p.getFecha().isAfter(finalFin))
                    .toList();
        } else if (inicio != null) {
            LocalDateTime finalInicio = inicio;
            pedidos = pedidos.stream()
                    .filter(p -> !p.getFecha().isBefore(finalInicio))
                    .toList();
        } else if (fin != null) {
            LocalDateTime finalFin = fin;
            pedidos = pedidos.stream()
                    .filter(p -> !p.getFecha().isAfter(finalFin))
                    .toList();
        }

        return pedidos;
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) validarDetallePedido(detalle);
        }
        Pedido nuevo = pedidoRepository.save(pedido);
        actualizarTotalPedido(nuevo.getId());
        return pedidoRepository.findById(nuevo.getId()).get();
    }

    @GetMapping("/{id}")
    public Pedido getPedidoById(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoDetalles) {
        return pedidoRepository.findById(id).map(pedido -> {

            pedido.setEstado(pedidoDetalles.getEstado());

            List<DetallePedido> nuevosDetalles = pedidoDetalles.getDetalles();
            if (nuevosDetalles != null) {
                for (DetallePedido detalle : nuevosDetalles) validarDetallePedido(detalle);
                pedido.setDetalles(nuevosDetalles);
            }

            Pedido actualizado = pedidoRepository.save(pedido);
            actualizarTotalPedido(actualizado.getId());

            return pedidoRepository.findById(actualizado.getId()).get();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        pedidoRepository.deleteById(id);
    }

    @GetMapping("/{id}/resumen")
    public Map<String, Object> generarResumenPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("id", pedido.getId());
        resumen.put("fecha", pedido.getFecha());
        resumen.put("total", pedido.getTotal());

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

    private void validarDetallePedido(DetallePedido detalle) {
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");

        if (detalle.getPrecio() == null || detalle.getPrecio().compareTo(BigDecimal.ZERO) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
    }

    private void actualizarTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        BigDecimal total = pedido.getDetalles() != null
                ? pedido.getDetalles().stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }
}
