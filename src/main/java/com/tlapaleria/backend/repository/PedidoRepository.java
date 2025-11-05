package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByEstadoAndFechaBefore(EstadoPedido estado, LocalDateTime fecha);

}
