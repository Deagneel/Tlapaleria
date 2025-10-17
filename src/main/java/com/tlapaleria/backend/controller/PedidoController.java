package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
}
