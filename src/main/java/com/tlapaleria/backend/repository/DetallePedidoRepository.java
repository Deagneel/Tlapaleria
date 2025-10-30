package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    boolean existsByProductoId(Long productoId);
}
