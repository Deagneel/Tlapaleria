package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.DetallePedido;
import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.repository.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-pedidos")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @GetMapping
    public List<DetallePedido> getAllDetallePedidos() {
        return detallePedidoRepository.findAll();
    }

    @PostMapping
    public DetallePedido crearDetallePedido(@RequestBody DetallePedido detallePedido) {
        if(detallePedido.getCantidad() == null || detallePedido.getCantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }

        if(detallePedido.getPrecio() == null || detallePedido.getPrecio() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
        }

        Producto producto = detallePedido.getProducto();
        if(producto.getExistencia() < detallePedido.getCantidad()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto: " + producto.getDescripcion());
        }
        return detallePedidoRepository.save(detallePedido);
    }

    @GetMapping("/{id}")
    public DetallePedido getDetallePedidoById(@PathVariable Long id) {
        return detallePedidoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public DetallePedido actualizarDetallePedido(@PathVariable Long id, @RequestBody DetallePedido detallePedidoDetalles) {
        return detallePedidoRepository.findById(id).map(detalle -> {
            if(detallePedidoDetalles.getCantidad() == null || detallePedidoDetalles.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");}
            if(detallePedidoDetalles.getPrecio() == null || detallePedidoDetalles.getPrecio() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");}
            Producto producto = detallePedidoDetalles.getProducto();
            if(producto.getExistencia() < detallePedidoDetalles.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto: " + producto.getDescripcion());}
            detalle.setCantidad(detallePedidoDetalles.getCantidad());
            detalle.setPrecio(detallePedidoDetalles.getPrecio());
            detalle.setProducto(detallePedidoDetalles.getProducto());
            detalle.setPedido(detallePedidoDetalles.getPedido());
            return detallePedidoRepository.save(detalle);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarDetallePedido(@PathVariable Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
