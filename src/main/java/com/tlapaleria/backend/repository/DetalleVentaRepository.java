package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.DetalleVenta;
import com.tlapaleria.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    boolean existsByProductoId(Long productoId);

    @Query("SELECT SUM(d.precio * d.cantidad) FROM DetalleVenta d WHERE d.venta.id = :ventaId")
    BigDecimal sumSubtotalesByVentaId(@Param("ventaId") Long ventaId);

    List<DetalleVenta> findByVentaId(Long ventaId);

    @Query("SELECT d FROM DetalleVenta d JOIN FETCH d.producto WHERE d.venta.id = :ventaId")
    List<DetalleVenta> findByVentaIdWithProducto(@Param("ventaId") Long ventaId);

    @Query("SELECT d.producto FROM DetalleVenta d WHERE d.venta.id IN :ventas")
    List<Producto> findProductByVenta(@Param("ventas") List<Long> ventas);
}
