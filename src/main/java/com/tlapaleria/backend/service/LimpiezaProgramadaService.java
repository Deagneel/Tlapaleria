package com.tlapaleria.backend.service;

import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.repository.PedidoRepository;
import com.tlapaleria.backend.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LimpiezaProgramadaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Limpia pedidos entregados hace más de 3 meses y productos temporales sin pedido.
     * Se ejecuta automáticamente los martes a las 2:23 AM.
     */
    @Scheduled(cron = "0 0 12 * * SUN") // Domingo 12 pm
    @Transactional
    public void limpiarPedidosYProductosAntiguos() {
        System.out.println("🧹 Ejecutando limpieza programada...");

        // Calcular la fecha límite (3 meses atrás, incluyendo hora)
        LocalDateTime haceTresMeses = LocalDateTime.now().minusMonths(3);

        // Buscar pedidos entregados antes de esa fecha
        List<Pedido> pedidosAntiguos = pedidoRepository.findByEstadoAndFechaBefore(EstadoPedido.ENTREGADO, haceTresMeses);
        System.out.println("🧾 Pedidos antiguos encontrados: " + pedidosAntiguos.size());

        // Eliminar los pedidos antiguos
        for (Pedido pedido : pedidosAntiguos) {
            pedidoRepository.delete(pedido);
            System.out.println("   🗑 Pedido eliminado: " + pedido.getId());
        }

        // Eliminar productos temporales sin pedidos asociados
        int eliminados = productoRepository.eliminarProductosTemporalesSinPedido();
        System.out.println("🗑 Productos temporales eliminados: " + eliminados);

        System.out.println("✅ Limpieza programada completada.");
    }
}
