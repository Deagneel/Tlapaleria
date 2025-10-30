package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetallePedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.repository.DetallePedidoRepository;
import com.tlapaleria.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/detalle-pedidos")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public List<DetallePedido> getAllDetallePedidos() {
        return detallePedidoRepository.findAll();
    }

    @PostMapping
    public DetallePedido crearDetallePedido(@RequestBody DetallePedido detallePedido) {
        // Si no se envía el precio, usar el precio individual del producto
        if (detallePedido.getPrecio() == null) {
            BigDecimal precioProducto = detallePedido.getProducto().getPrecioIndividual();
            detallePedido.setPrecio(precioProducto != null ? precioProducto : BigDecimal.ZERO);
        }

        validarDetalle(detallePedido);

        DetallePedido nuevo = detallePedidoRepository.save(detallePedido);
        actualizarTotalPedido(nuevo.getPedido().getId());

        return nuevo;
    }

    @GetMapping("/{id}")
    public DetallePedido getDetallePedidoById(@PathVariable Long id) {
        return detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
    }

    @PutMapping("/{id}")
    public DetallePedido actualizarDetallePedido(@PathVariable Long id, @RequestBody DetallePedido detallePedidoDetalles) {
        // Si no se envía el precio, usar el precio individual del producto
        if (detallePedidoDetalles.getPrecio() == null) {
            BigDecimal precioProducto = detallePedidoDetalles.getProducto().getPrecioIndividual();
            detallePedidoDetalles.setPrecio(precioProducto != null ? precioProducto : BigDecimal.ZERO);
        }

        return detallePedidoRepository.findById(id).map(detalle -> {
            validarDetalle(detallePedidoDetalles);

            detalle.setCantidad(detallePedidoDetalles.getCantidad());
            detalle.setPrecio(detallePedidoDetalles.getPrecio());
            detalle.setProducto(detallePedidoDetalles.getProducto());
            detalle.setPedido(detallePedidoDetalles.getPedido());

            DetallePedido actualizado = detallePedidoRepository.save(detalle);
            actualizarTotalPedido(actualizado.getPedido().getId());

            return actualizado;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de pedido no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarDetallePedido(@PathVariable Long id) {
        DetallePedido detalle = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));

        Long pedidoId = detalle.getPedido().getId();
        detallePedidoRepository.deleteById(id);
        actualizarTotalPedido(pedidoId);
    }

    private void validarDetalle(DetallePedido detallePedido) {
        if (detallePedido.getCantidad() == null || detallePedido.getCantidad() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");

        if (detallePedido.getPrecio() == null || detallePedido.getPrecio().compareTo(BigDecimal.ZERO) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");

        Producto producto = detallePedido.getProducto();
        if (producto == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no puede ser nulo");
    }

    private void actualizarTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));


        BigDecimal nuevoTotal = pedido.getDetalles()
                .stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setTotal(nuevoTotal);
        pedidoRepository.save(pedido);
    }
}
