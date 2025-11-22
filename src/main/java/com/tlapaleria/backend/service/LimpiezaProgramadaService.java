package com.tlapaleria.backend.service;

import com.tlapaleria.backend.model.EstadoPedido;
import com.tlapaleria.backend.model.Pedido;
import com.tlapaleria.backend.model.Venta;
import com.tlapaleria.backend.repository.PedidoRepository;
import com.tlapaleria.backend.repository.VentaRepository;
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

    @Autowired
    private VentaRepository ventaRepository;

    /**
     * Limpia pedidos entregados hace más de 3 meses y productos temporales sin pedido.
     * Se ejecuta automáticamente los domingos a las 12 pm.
     */
    @Scheduled(cron = "0 0 12 * * SUN")
    @Transactional
    public void limpiarPedidosYProductosAntiguos() {
        System.out.println(" Ejecutando limpieza programada...");

        LocalDateTime haceTresMeses = LocalDateTime.now().minusMonths(3);

        List<Pedido> pedidosAntiguos = pedidoRepository.findByEstadoAndFechaBefore(EstadoPedido.ENTREGADO, haceTresMeses);
        System.out.println(" Pedidos antiguos encontrados: " + pedidosAntiguos.size());

        for (Pedido pedido : pedidosAntiguos) {
            pedidoRepository.delete(pedido);
            System.out.println("    Pedido eliminado: " + pedido.getId());
        }

        int eliminados = productoRepository.eliminarProductosTemporalesSinPedido();
        System.out.println("Productos temporales eliminados: " + eliminados);

        System.out.println("Limpieza programada completada.");
    }

    /**
     * Limpia ventas de más de 5 años con sus detalles
     * Se ejecuta automáticamente los domingos a las 12 pm.
     */

    @Scheduled(cron = "0 0 12 * * SUN")
    @Transactional
    public void limpiarVentasAntiguas() {
        System.out.println(" Ejecutando limpieza de ventas antiguas...");

        LocalDateTime limite = LocalDateTime.now().minusYears(5);

        List<Venta> ventasAntiguas = ventaRepository.findVentasAntesDe(limite);
        System.out.println(" Ventas encontradas para eliminar: " + ventasAntiguas.size());

        for (Venta venta : ventasAntiguas) {
            ventaRepository.delete(venta);
            System.out.println("    Venta eliminada: " + venta.getId());
        }

        System.out.println("Limpieza de ventas antiguas completada.");
    }
}
