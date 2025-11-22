package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    boolean existsByProductoId(Long productoId);

    @Query("SELECT SUM(d.precio * d.cantidad) FROM DetalleVenta d WHERE d.venta.id = :ventaId")
    BigDecimal sumSubtotalesByVentaId(@Param("ventaId") Long ventaId);
}
