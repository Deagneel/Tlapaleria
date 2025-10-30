package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.*;
import com.tlapaleria.backend.repository.DetallePedidoRepository;
import com.tlapaleria.backend.repository.PedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // ---------------- GET ----------------
    @GetMapping
    public List<PedidoResumenDTO> obtenerPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(p -> new PedidoResumenDTO(
                        p.getId(),
                        p.getCliente(),
                        p.getFecha(),
                        p.getEstado().name(),
                        p.getTotal()
                )).collect(Collectors.toList());
    }

    @GetMapping("/completo")
    public List<PedidoCompletoDTO> obtenerPedidosCompletos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream().map(p -> {
            PedidoCompletoDTO dto = new PedidoCompletoDTO();
            dto.setId(p.getId());
            dto.setCliente(p.getCliente());
            dto.setEstado(p.getEstado().name());
            dto.setTotal(p.getTotal());

            List<PedidoCompletoDTO.DetallePedidoDTO> detalles = p.getDetalles().stream()
                    .map(d -> {
                        PedidoCompletoDTO.DetallePedidoDTO detalleDTO = new PedidoCompletoDTO.DetallePedidoDTO();
                        detalleDTO.setId(d.getId());
                        detalleDTO.setProducto_id(d.getProducto().getId());
                        detalleDTO.setCantidad(d.getCantidad());
                        detalleDTO.setPrecio(d.getPrecio());
                        return detalleDTO;
                    }).collect(Collectors.toList());

            dto.setDetalles(detalles);
            return dto;
        }).collect(Collectors.toList());
    }

    // ---------------- POST ----------------
    @PostMapping
    public ResponseEntity<PedidoCompletoDTO> crearPedido(@RequestBody PedidoCompletoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCliente(dto.getCliente());
        pedido.setEstado(EstadoPedido.valueOf(dto.getEstado()));
        pedido.setTotal(dto.getTotal());
        pedido.setFecha(java.time.LocalDateTime.now());

        List<DetallePedido> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecio(d.getPrecio());

            Producto producto = new Producto();
            producto.setId(d.getProducto_id()); // Solo asignamos el ID, debe existir en DB
            detalle.setProducto(producto);

            return detalle;
        }).collect(Collectors.toList());

        pedido.setDetalles(detalles);

        Pedido guardado = pedidoRepository.save(pedido);

        dto.setId(guardado.getId());
        for (int i = 0; i < detalles.size(); i++) {
            dto.getDetalles().get(i).setId(detalles.get(i).getId());
        }

        return ResponseEntity.ok(dto);
    }

    // ---------------- PUT ----------------
    @PutMapping("/{id}")
    public ResponseEntity<PedidoCompletoDTO> actualizarPedido(@PathVariable Long id,
                                                              @RequestBody PedidoCompletoDTO dto) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        if (optionalPedido.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = optionalPedido.get();
        pedido.setCliente(dto.getCliente());
        pedido.setEstado(EstadoPedido.valueOf(dto.getEstado()));
        pedido.setTotal(dto.getTotal());

        // Eliminar detalles que no están en el DTO
        List<Long> idsDTO = dto.getDetalles().stream()
                .map(PedidoCompletoDTO.DetallePedidoDTO::getId)
                .collect(Collectors.toList());
        pedido.getDetalles().removeIf(d -> d.getId() != null && !idsDTO.contains(d.getId()));

        // Actualizar/Agregar detalles
        for (PedidoCompletoDTO.DetallePedidoDTO d : dto.getDetalles()) {
            DetallePedido detalle;
            if (d.getId() != null) {
                // actualizar existente
                detalle = pedido.getDetalles().stream()
                        .filter(pd -> pd.getId().equals(d.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            DetallePedido nuevo = new DetallePedido();
                            nuevo.setPedido(pedido);
                            pedido.getDetalles().add(nuevo);
                            return nuevo;
                        });
            } else {
                // nuevo detalle
                detalle = new DetallePedido();
                detalle.setPedido(pedido);
                pedido.getDetalles().add(detalle);
            }

            detalle.setCantidad(d.getCantidad());
            detalle.setPrecio(d.getPrecio());

            Producto producto = new Producto();
            producto.setId(d.getProducto_id());
            detalle.setProducto(producto);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        dto.setId(guardado.getId());
        return ResponseEntity.ok(dto);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        if (optionalPedido.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        pedidoRepository.delete(optionalPedido.get());
        return ResponseEntity.noContent().build();
    }
}
