package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    // Buscar un detalle por el id del pedido y del producto
    Optional<DetallePedido> findByPedidoIdAndProductoId(Long pedidoId, Long productoId);
}
