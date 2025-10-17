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

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // 🔹 Obtener todos los pedidos o filtrado por estado
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

    // 🔹 Obtener resumen de todos los pedidos
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
            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                inicio = LocalDateTime.parse(fechaInicio);
            }
            if (fechaFin != null && !fechaFin.isEmpty()) {
                fin = LocalDateTime.parse(fechaFin);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido");
        }

        List<Pedido> pedidos = pedidoRepository.findAll(); // inicializamos con todos

        if (estadoEnum != null) {
            pedidos = pedidoRepository.findByEstado(estadoEnum);
        }

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


    // 🔹 Crear pedido con validación y total automático
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                validarDetallePedido(detalle);
            }
        }
        Pedido nuevo = pedidoRepository.save(pedido);
        actualizarTotalPedido(nuevo.getId());
        return pedidoRepository.findById(nuevo.getId()).get();
    }

    // 🔹 Obtener pedido por ID
    @GetMapping("/{id}")
    public Pedido getPedidoById(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    // 🔹 Actualizar pedido con validación y total automático
    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoDetalles) {
        return pedidoRepository.findById(id).map(pedido -> {

            // Actualizamos estado
            pedido.setEstado(pedidoDetalles.getEstado());

            // Validamos y actualizamos detalles
            List<DetallePedido> nuevosDetalles = pedidoDetalles.getDetalles();
            if (nuevosDetalles != null) {
                for (DetallePedido detalle : nuevosDetalles) {
                    validarDetallePedido(detalle);
                }
                pedido.setDetalles(nuevosDetalles);
            }

            Pedido actualizado = pedidoRepository.save(pedido);
            actualizarTotalPedido(actualizado.getId());

            return pedidoRepository.findById(actualizado.getId()).get();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    // 🔹 Eliminar pedido
    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        pedidoRepository.deleteById(id);
    }

    // 🔹 Resumen simplificado de un pedido (para enviar a proveedor)
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

    // Validar cada detalle del pedido
    private void validarDetallePedido(DetallePedido detalle) {
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");

        if (detalle.getPrecio() == null || detalle.getPrecio() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");

        Producto producto = detalle.getProducto();
        if (producto == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no puede ser nulo");

        if (producto.getExistencia() != null && producto.getExistencia() < detalle.getCantidad())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para el producto: " + producto.getDescripcion());
    }

    // Recalcular total del pedido
    private void actualizarTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        double total = pedido.getDetalles() != null ?
                pedido.getDetalles().stream().mapToDouble(DetallePedido::getSubtotal).sum() : 0.0;

        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }
}
