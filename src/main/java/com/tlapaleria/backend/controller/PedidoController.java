package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.*;
import com.tlapaleria.backend.repository.DetallePedidoRepository;
import com.tlapaleria.backend.repository.PedidoRepository;
import com.tlapaleria.backend.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            DetallePedidoRepository detallePedidoRepository,
                            ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    // ---------------- GET: resumen ----------------
    @GetMapping
    public List<PedidoResumenDTO> obtenerTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(p -> new PedidoResumenDTO(
                        p.getId(),
                        p.getCliente(),
                        p.getFecha(),
                        p.getEstado() != null ? p.getEstado().name() : null,
                        p.getTotal()
                ))
                .collect(Collectors.toList());
    }

    // ---------------- GET: completo (lista) ----------------
    @GetMapping("/completo")
    public List<PedidoCompletoDTO> obtenerTodosCompletos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream().map(this::mapPedidoACompletoDTO).collect(Collectors.toList());
    }

    // ---------------- GET por id (completo) ----------------
    @GetMapping("/{id}")
    public ResponseEntity<PedidoCompletoDTO> obtenerPorId(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) return ResponseEntity.notFound().build();
        PedidoCompletoDTO dto = mapPedidoACompletoDTO(pedidoOpt.get());
        return ResponseEntity.ok(dto);
    }

    // ---------------- POST ----------------
    @PostMapping
    @Transactional
    public ResponseEntity<PedidoCompletoDTO> crearPedido(@RequestBody PedidoCompletoDTO dto) {
        try {
            Pedido pedido = new Pedido();
            pedido.setCliente(dto.getCliente());
            pedido.setFecha(LocalDateTime.now());
            pedido.setEstado(dto.getEstado() != null ? EstadoPedido.valueOf(dto.getEstado()) : EstadoPedido.PENDIENTE);

            List<DetallePedido> detalles = new ArrayList<>();
            if (dto.getDetalles() != null) {
                for (PedidoCompletoDTO.DetallePedidoDTO detDto : dto.getDetalles()) {
                    if (detDto.getProducto_id() == null) continue;
                    Optional<Producto> prodOpt = productoRepository.findById(detDto.getProducto_id());
                    if (prodOpt.isEmpty()) continue;

                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedido(pedido);
                    detalle.setProducto(prodOpt.get());
                    detalle.setCantidad(detDto.getCantidad());
                    detalle.setPrecio(detDto.getPrecio());
                    if (detDto.getRecibido() != null) detalle.setRecibido(detDto.getRecibido());
                    detalles.add(detalle);
                }
            }

            pedido.setDetalles(detalles);
            BigDecimal total = detalles.stream()
                    .map(DetallePedido::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            pedido.setTotal(total);

            Pedido guardado = pedidoRepository.save(pedido);
            PedidoCompletoDTO resp = mapPedidoACompletoDTO(guardado);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ---------------- PUT: actualizar pedido ----------------
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PedidoCompletoDTO> actualizarPedido(@PathVariable Long id,
                                                              @RequestBody PedidoCompletoDTO dto) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            Pedido pedido = pedidoOpt.get();
            pedido.setCliente(dto.getCliente());
            if (dto.getEstado() != null) {
                pedido.setEstado(EstadoPedido.valueOf(dto.getEstado()));
            }

            // Limpiamos y regeneramos los detalles sobre la MISMA colección
            List<DetallePedido> actuales = pedido.getDetalles();
            if (actuales == null) {
                actuales = new ArrayList<>();
                pedido.setDetalles(actuales);
            } else {
                actuales.clear();
            }

            if (dto.getDetalles() != null) {
                for (PedidoCompletoDTO.DetallePedidoDTO detDto : dto.getDetalles()) {
                    if (detDto.getProducto_id() == null) continue;
                    Optional<Producto> prodOpt = productoRepository.findById(detDto.getProducto_id());
                    if (prodOpt.isEmpty()) continue;

                    DetallePedido det = new DetallePedido();
                    det.setPedido(pedido);
                    det.setProducto(prodOpt.get());
                    det.setCantidad(detDto.getCantidad());
                    det.setPrecio(detDto.getPrecio());
                    if (detDto.getRecibido() != null) det.setRecibido(detDto.getRecibido());
                    actuales.add(det);
                }
            }

            // ✅ Calcular total automáticamente
            BigDecimal total = actuales.stream()
                    .map(DetallePedido::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            pedido.setTotal(total);

            Pedido guardado = pedidoRepository.save(pedido);
            PedidoCompletoDTO resp = mapPedidoACompletoDTO(guardado);
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // ---------------- PATCH: cambiar estado ----------------
    @PatchMapping("/{id}/estado")
    @Transactional
    public ResponseEntity<PedidoCompletoDTO> cambiarEstado(@PathVariable Long id,
                                                           @RequestParam String nuevoEstado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            Pedido pedido = pedidoOpt.get();
            EstadoPedido destino = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
            pedido.setEstado(destino);

            if (destino == EstadoPedido.SURTIDO) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    if (detalle.isRecibido()) { // Solo los marcados como recibidos
                        Producto producto = detalle.getProducto();
                        if (producto != null) {
                            Integer existenciaActual = producto.getExistencia();
                            if (existenciaActual == null) existenciaActual = 0;

                            Integer cantidadRecibida = detalle.getCantidad() != null ? detalle.getCantidad() : 0;

                            Integer nuevaExistencia = existenciaActual + cantidadRecibida;
                            producto.setExistencia(nuevaExistencia);

                            // ⚠️ No modificamos existencia_min ni otros campos
                            productoRepository.save(producto);
                        }
                    }
                }
            }

            if (destino == EstadoPedido.ENTREGADO) {
                List<Producto> inactivos = productoRepository.findAll().stream()
                        .filter(p -> Boolean.FALSE.equals(p.getActivo()))
                        .collect(Collectors.toList());
                if (!inactivos.isEmpty()) {
                    productoRepository.deleteAll(inactivos);
                }
            }

            Pedido actualizado = pedidoRepository.save(pedido);
            PedidoCompletoDTO dto = mapPedidoACompletoDTO(actualizado);
            return ResponseEntity.ok(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) return ResponseEntity.notFound().build();
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Helpers: mapeos a DTOs ----------------
    private PedidoCompletoDTO mapPedidoACompletoDTO(Pedido p) {
        PedidoCompletoDTO dto = new PedidoCompletoDTO();
        dto.setId(p.getId());
        dto.setCliente(p.getCliente());
        dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
        dto.setTotal(p.getTotal());

        List<PedidoCompletoDTO.DetallePedidoDTO> detalles = (p.getDetalles() == null) ? Collections.emptyList()
                : p.getDetalles().stream().map(d -> {
            PedidoCompletoDTO.DetallePedidoDTO dd = new PedidoCompletoDTO.DetallePedidoDTO();
            dd.setId(d.getId());
            dd.setProducto_id(d.getProducto().getId());
            dd.setCantidad(d.getCantidad());
            dd.setPrecio(d.getPrecio());
            dd.setRecibido(d.isRecibido());

            // Incluimos el producto completo
            dd.setProducto(new ProductoDTO(
                    d.getProducto().getId(),
                    d.getProducto().getClave(),
                    d.getProducto().getDescripcion(),
                    d.getProducto().getCodigo_barras(),
                    d.getProducto().getCosto(),
                    d.getProducto().getPrecio(),
                    d.getProducto().getPrecioIndividual(),
                    d.getProducto().getActivo()
            ));

            return dd;
        }).collect(Collectors.toList());

        dto.setDetalles(detalles);
        return dto;
    }
}
