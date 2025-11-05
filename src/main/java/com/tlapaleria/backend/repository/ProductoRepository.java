package com.tlapaleria.backend.repository;

import com.tlapaleria.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoFalse();

    @Modifying
    @Transactional
    @Query("DELETE FROM Producto p WHERE p.activo = false AND p.id NOT IN (SELECT DISTINCT dp.producto.id FROM DetallePedido dp)")
    int eliminarProductosTemporalesSinPedido();
}
