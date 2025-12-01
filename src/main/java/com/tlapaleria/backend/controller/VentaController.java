package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.*;
import com.tlapaleria.backend.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<?> listarVentas() {
        return ResponseEntity.ok(ventaService.obtenerVentas());
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<VentaResumenDTO>> obtenerResumenVentas() {
        List<VentaResumenDTO> resumen = ventaService.obtenerResumenVentas();
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Long id) {
        VentaDetalleDTO dto = ventaService.obtenerVentaDetalle(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> crearVenta(@RequestBody VentaDTO dto) {
        VentaResponseDTO resp = ventaService.crearVenta(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/cambio")
    public ResponseEntity<?> calcularCambio(@RequestParam double total,
                                            @RequestParam double pagoCon) {

        double cambio = Math.round((pagoCon - total) * 100.0) / 100.0;
        if (cambio <= 0) {
            return ResponseEntity.ok(Map.of(
                    "cambio", 0,
                    "desglose", Map.of()
            ));
        }

        Map<String, Integer> desglose = ventaService.calcularDesgloseCambio(cambio);
        return ResponseEntity.ok(Map.of(
                "cambio", cambio,
                "desglose", desglose
        ));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialWeekDTO>> obtenerSemanas() {
        List<HistorialWeekDTO> semanas = ventaService.obtenerSemanasDisponibles();
        return ResponseEntity.ok(semanas);
    }

    @GetMapping("/historial/{year}/{week}")
    public ResponseEntity<List<HistorialDayDTO>> obtenerHistorialSemana(
            @PathVariable int year,
            @PathVariable int week) {

        List<HistorialDayDTO> dias = ventaService.obtenerHistorialPorSemana(year, week);
        return ResponseEntity.ok(dias);
    }

    @GetMapping("/historial/venta/{id}")
    public ResponseEntity<?> obtenerVentaDetalle(@PathVariable Long id) {
        VentaDetalleDTO dto = ventaService.obtenerVentaDetalle(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/historial/meses")
    public ResponseEntity<List<HistorialMonthDTO>> obtenerMeses() {
        List<HistorialMonthDTO> meses = ventaService.obtenerMesesDisponibles();
        return ResponseEntity.ok(meses);
    }

    @GetMapping("/historial/{year}/mes/{month}/semanas")
    public ResponseEntity<List<HistorialWeekDTO>> obtenerSemanasPorMes(
            @PathVariable int year,
            @PathVariable int month) {
        List<HistorialWeekDTO> semanas = ventaService.obtenerSemanasPorMes(year, month);
        return ResponseEntity.ok(semanas);
    }

    @GetMapping("/historial/dia/{date}")
    public ResponseEntity<List<VentaResumenHistorialDTO>> obtenerVentasPorDia(
            @PathVariable String date) {
        List<VentaResumenHistorialDTO> ventas = ventaService.obtenerVentasPorDia(LocalDate.parse(date));
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/historial/dia/{date}/productos")
    public ResponseEntity<List<ProductoVendidoDTO>> obtenerProductosVendidosEnDia(
            @PathVariable String date) {
        List<ProductoVendidoDTO> productos = ventaService.obtenerProductosVendidosPorDia(LocalDate.parse(date));
        return ResponseEntity.ok(productos);
    }


    @GetMapping("/historial/anios")
    public ResponseEntity<List<HistorialYearDTO>> obtenerAnios() {
        List<HistorialYearDTO> años = ventaService.obtenerAniosDisponibles();
        return ResponseEntity.ok(años);
    }


    @DeleteMapping("/venta/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/detalle/{detalleId}")
    public ResponseEntity<VentaDetalleDTO> eliminarDetalle(@PathVariable Long detalleId) {
        VentaDetalleDTO dto = ventaService.eliminarDetalleVenta(detalleId);
        return ResponseEntity.ok(dto);
    }



    @DeleteMapping("/historial/dia/{date}/producto/{productoId}")
    public ResponseEntity<?> eliminarProductoEnDia(@PathVariable String date, @PathVariable Long productoId) {
        LocalDate d = LocalDate.parse(date);
        ventaService.eliminarProductoVendidosEnDia(d, productoId);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/dto")
    public ResponseEntity<?> listarVentasDTO() {
        return ResponseEntity.ok(ventaService.listarVentasDTO());
    }

}
