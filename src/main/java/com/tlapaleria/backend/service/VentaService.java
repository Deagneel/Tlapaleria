package com.tlapaleria.backend.service;

import com.tlapaleria.backend.model.*;
import com.tlapaleria.backend.repository.DetalleVentaRepository;
import com.tlapaleria.backend.repository.ProductoRepository;
import com.tlapaleria.backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.time.YearMonth;


@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductoRepository productoRepository,
                        DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }


    // ---------------- Nuevo: años disponibles ----------------
    public List<HistorialYearDTO> obtenerAniosDisponibles() {
        List<Venta> ventas = ventaRepository.findAll();
        if (ventas.isEmpty()) return Collections.emptyList();


        Map<Integer, List<Venta>> porAno = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().getYear()));


        List<HistorialYearDTO> años = porAno.entrySet().stream().map(e -> {
            int y = e.getKey();
            List<Venta> list = e.getValue();
            LocalDate start = LocalDate.of(y, 1, 1);
            LocalDate end = LocalDate.of(y, 12, 31);
            BigDecimal total = list.stream()
                    .map(Venta::getTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            return new HistorialYearDTO(y, start, end, list.size(), total);
        }).sorted(Comparator.comparing(HistorialYearDTO::getYear).reversed())
           .collect(Collectors.toList());

        return años;
    }

    // ---------------- Eliminar venta completa (y restaurar existencias) ----------------
    @Transactional
    public void eliminarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));


// restaurar inventario por cada detalle
        for (DetalleVenta det : venta.getDetalles()) {
            Producto prod = productoRepository.findById(det.getProducto().getId()).orElse(null);
            if (prod == null) continue;
            int actual = prod.getExistencia() != null ? prod.getExistencia() : 0;
            int suma = det.getCantidad() != null ? det.getCantidad() : 0;
            prod.setExistencia(actual + suma);
            productoRepository.save(prod);
        }


// eliminar la venta (cascade eliminará detalles)
        ventaRepository.delete(venta);
    }


    // ---------------- Eliminar detalle individual ----------------
    @Transactional
    public VentaDetalleDTO eliminarDetalleVenta(Long detalleId) {
        DetalleVenta det = detalleVentaRepository.findById(detalleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de venta no encontrado"));

        Venta venta = det.getVenta();
        Producto prod = det.getProducto();

        // ✔️ Restaurar inventario
        int actual = prod.getExistencia() != null ? prod.getExistencia() : 0;
        int suma = det.getCantidad() != null ? det.getCantidad() : 0;
        prod.setExistencia(actual + suma);
        productoRepository.save(prod);

        // ✔️ Eliminar detalle
        detalleVentaRepository.delete(det);

        // ✔️ Recalcular total de la venta
        BigDecimal nuevoTotal = detalleVentaRepository.sumSubtotalesByVentaId(venta.getId());
        if (nuevoTotal == null) nuevoTotal = BigDecimal.ZERO;

        venta.setTotal(nuevoTotal);
        ventaRepository.save(venta);

        // ✔️ Regresamos el DTO actualizado
        return obtenerVentaDetalle(venta.getId());
    }



    // ---------------- Eliminar todos los detalles de un producto en un día (devolución por día) ----------------
    @Transactional
    public void eliminarProductoVendidosEnDia(LocalDate date, Long productoId) {
        LocalDateTime inicio = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23,59,59, 999_999_999);


        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);


        for (Venta v : ventas) {
            List<DetalleVenta> toRemove = v.getDetalles().stream()
                    .filter(d -> d.getProducto() != null && Objects.equals(d.getProducto().getId(), productoId))
                    .collect(Collectors.toList());


            for (DetalleVenta det : toRemove) {
// restaurar inventario
                Producto prod = productoRepository.findById(productoId).orElse(null);
                if (prod != null) {
                    int actual = prod.getExistencia() != null ? prod.getExistencia() : 0;
                    int suma = det.getCantidad() != null ? det.getCantidad() : 0;
                    prod.setExistencia(actual + suma);
                    productoRepository.save(prod);
                }


// eliminar detalle
                detalleVentaRepository.deleteById(det.getId());
            }
        }
    }



    @Transactional
    public VentaResponseDTO crearVenta(VentaDTO dto) {
        if (dto == null) throw new IllegalArgumentException("VentaDTO es nulo");

        // Construir entidad Venta
        Venta venta = new Venta();

        List<DetalleVenta> detalles = new ArrayList<>();
        if (dto.getDetalles() != null) {
            for (DetalleVentaDTO d : dto.getDetalles()) {
                if (d.getProductoId() == null) continue;

                Producto prod = productoRepository.findById(d.getProductoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Producto no encontrado: " + d.getProductoId()));

                DetalleVenta det = new DetalleVenta();
                det.setVenta(venta);
                det.setProducto(prod);
                det.setCantidad(d.getCantidad() != null ? d.getCantidad() : 0);

                // 🔹 Usar precio enviado por frontend, o fallback al precio individual o normal del producto
                BigDecimal precioFinal = d.getPrecio() != null
                        ? d.getPrecio()
                        : (d.getPrecioIndividual() != null
                        ? d.getPrecioIndividual()
                        : prod.getPrecio());

                det.setPrecio(precioFinal);
                det.setPrecioIndividual(d.getPrecioIndividual() != null ? d.getPrecioIndividual() : prod.getPrecio());

                detalles.add(det);
            }
        }
        venta.setDetalles(detalles);

        // Calcular total
        BigDecimal subtotal = detalles.stream()
                .map(DetalleVenta::getSubtotal) // subtotal = precio * cantidad
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cargoExtra = dto.getCargoExtra() != null ? dto.getCargoExtra() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(cargoExtra);

        venta.setCargo_extra(cargoExtra);
        venta.setTotal(total);

        venta.setPago_con(dto.getPagoCon() != null ? dto.getPagoCon() : BigDecimal.ZERO);
        venta.setCambio(venta.getPago_con().compareTo(total) >= 0
                ? venta.getPago_con().subtract(total)
                : BigDecimal.ZERO);

        venta.setFecha(LocalDateTime.now());

        // Guardar venta y detalles (cascade)
        Venta saved = ventaRepository.save(venta);

        // Actualizar inventario y obtener lowStock
        List<ProductoLowStockDTO> lowStock = new ArrayList<>();
        for (DetalleVenta det : saved.getDetalles()) {
            Producto producto = productoRepository.findById(det.getProducto().getId()).orElse(null);
            if (producto == null) continue;

            int actual = producto.getExistencia() != null ? producto.getExistencia() : 0;
            int nueva = actual - (det.getCantidad() != null ? det.getCantidad() : 0);
            producto.setExistencia(nueva);
            productoRepository.save(producto);

            int min = producto.getExistencia_min() != null ? producto.getExistencia_min() : 0;
            if (nueva <= min) {
                lowStock.add(new ProductoLowStockDTO(
                        producto.getId(),
                        producto.getClave(),
                        producto.getDescripcion(),
                        nueva,
                        min
                ));
            }
        }

        // Mapear entidad Venta -> VentaDetalleDTO (respuesta)
        VentaDetalleDTO ventaDetalleDTO = new VentaDetalleDTO();
        ventaDetalleDTO.setId(saved.getId());
        ventaDetalleDTO.setTotal(saved.getTotal());
        ventaDetalleDTO.setPagoCon(saved.getPago_con());
        ventaDetalleDTO.setCambio(saved.getCambio());
        ventaDetalleDTO.setCargoExtra(saved.getCargo_extra());
        ventaDetalleDTO.setFecha(saved.getFecha());

        List<DetalleVentaResultDTO> detallesResp = saved.getDetalles().stream().map(det -> {
            DetalleVentaResultDTO dr = new DetalleVentaResultDTO();
            dr.setId(det.getId());
            dr.setProductoId(det.getProducto().getId());
            dr.setClave(det.getProducto().getClave());
            dr.setDescripcion(det.getProducto().getDescripcion());
            dr.setCantidad(det.getCantidad());
            dr.setPrecio(det.getPrecio());           // ahora refleja precio correcto
            dr.setSubtotal(det.getSubtotal());

            // 🔹 Precio individual también enviado
            dr.setPrecioIndividual(det.getPrecioIndividual());

            dr.setExistencia(det.getProducto().getExistencia());
            dr.setExistenciaMin(det.getProducto().getExistencia_min());
            return dr;
        }).collect(Collectors.toList());

        ventaDetalleDTO.setDetalles(detallesResp);

        VentaResponseDTO response = new VentaResponseDTO();
        response.setVenta(ventaDetalleDTO);
        response.setLowStock(lowStock);

        return response;
    }


    public List<Venta> obtenerVentas() {
        return ventaRepository.findAll();
    }

    public List<VentaResumenDTO> obtenerResumenVentas() {
        List<Venta> ventas = ventaRepository.findAll();

        return ventas.stream()
                .map(v -> new VentaResumenDTO(
                        v.getId(),
                        v.getFecha(),
                        v.getTotal()
                ))
                .sorted(Comparator.comparing(VentaResumenDTO::getFecha).reversed())
                .collect(Collectors.toList());
    }


    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }

    // ---------------- Historial: semanas disponibles ----------------
    public List<HistorialWeekDTO> obtenerSemanasDisponibles() {
        List<Venta> ventas = ventaRepository.findAll();
        if (ventas.isEmpty()) return Collections.emptyList();

        WeekFields wf = WeekFields.ISO;
        // agrupar por (year, week)
        Map<String, List<Venta>> porSemana = ventas.stream().collect(Collectors.groupingBy(v -> {
            LocalDate date = v.getFecha().toLocalDate();
            int week = date.get(wf.weekOfWeekBasedYear());
            int year = date.get(wf.weekBasedYear());
            return year + "-" + week;
        }));

        List<HistorialWeekDTO> semanas = new ArrayList<>();
        for (Map.Entry<String, List<Venta>> e : porSemana.entrySet()) {
            String[] parts = e.getKey().split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            List<Venta> vlist = e.getValue();

            // calcular inicio y fin de semana ISO
            LocalDate anyDate = vlist.get(0).getFecha().toLocalDate();
            LocalDate start = anyDate.with(wf.dayOfWeek(), 1);
            // ajustar al week/year
            start = LocalDate.now()
                    .withYear(year)
                    .with(wf.weekOfWeekBasedYear(), week)
                    .with(wf.dayOfWeek(), 1);

            LocalDate end = start.with(wf.dayOfWeek(), 7);

            BigDecimal totalSemana = vlist.stream()
                    .map(Venta::getTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            semanas.add(new HistorialWeekDTO(year, week, start, end, vlist.size(), totalSemana));
        }

        // ordenar por (year desc, week desc)
        semanas.sort(Comparator.comparing(HistorialWeekDTO::getYear).reversed()
                .thenComparing(HistorialWeekDTO::getWeek).reversed());

        return semanas;
    }

    // ---------------- Historial: ventas por semana agrupadas por día ----------------
    public List<HistorialDayDTO> obtenerHistorialPorSemana(int year, int week) {
        List<Venta> ventas = ventaRepository.findAll(); // dataset pequeño, usar findAll y filtrar

        WeekFields wf = WeekFields.ISO;
        // filtrar ventas de esa week/year
        List<Venta> ventasSemana = ventas.stream().filter(v -> {
            LocalDate date = v.getFecha().toLocalDate();
            int w = date.get(wf.weekOfWeekBasedYear());
            int y = date.get(wf.weekBasedYear());
            return w == week && y == year;
        }).collect(Collectors.toList());

        if (ventasSemana.isEmpty()) return Collections.emptyList();

        // Agrupar por día (LocalDate)
        Map<LocalDate, List<Venta>> porDia = ventasSemana.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate()));

        // Para cada día construimos HistorialDayDTO
        List<HistorialDayDTO> dias = porDia.entrySet().stream().map(entry -> {
            LocalDate dia = entry.getKey();
            List<Venta> lista = entry.getValue();

            List<VentaResumenHistorialDTO> resumenVentas = lista.stream().map(v -> {
                VentaResumenHistorialDTO r = new VentaResumenHistorialDTO();
                r.setId(v.getId());
                r.setFecha(v.getFecha());
                r.setTotal(v.getTotal());
                r.setLineas(v.getDetalles() != null ? v.getDetalles().size() : 0);
                return r;
            }).sorted(Comparator.comparing(VentaResumenHistorialDTO::getFecha)).collect(Collectors.toList());

            BigDecimal totalDia = lista.stream()
                    .map(Venta::getTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new HistorialDayDTO(dia, lista.size(), totalDia, resumenVentas);
        }).sorted(Comparator.comparing(HistorialDayDTO::getDate)).collect(Collectors.toList());

        return dias;
    }

    // ---------------- Historial: detalle completo de una venta (usa VentaDetalleDTO) ----------------
    @Transactional(readOnly = true)
    public VentaDetalleDTO obtenerVentaDetalle(Long id) {
        Venta v = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        VentaDetalleDTO dto = new VentaDetalleDTO();
        dto.setId(v.getId());
        dto.setTotal(v.getTotal());
        dto.setPagoCon(v.getPago_con());
        dto.setCambio(v.getCambio());
        dto.setCargoExtra(v.getCargo_extra());
        dto.setFecha(v.getFecha());

        List<DetalleVentaResultDTO> detalles = v.getDetalles().stream().map(det -> {
            DetalleVentaResultDTO dr = new DetalleVentaResultDTO();
            dr.setId(det.getId());
            dr.setProductoId(det.getProducto().getId());
            dr.setClave(det.getProducto().getClave());
            dr.setDescripcion(det.getProducto().getDescripcion());
            dr.setCantidad(det.getCantidad());
            dr.setPrecio(det.getPrecio());
            dr.setSubtotal(det.getSubtotal());

            dr.setExistencia(det.getProducto().getExistencia());
            dr.setExistenciaMin(det.getProducto().getExistencia_min());

            return dr;
        }).collect(Collectors.toList());


        dto.setDetalles(detalles);
        return dto;
    }

    public Map<String, Integer> calcularDesgloseCambio(double cambioDouble) {
        double[] denoms = {500, 200, 100, 50, 20, 10, 5, 2, 1, 0.5};
        Map<String, Integer> desglose = new LinkedHashMap<>();
        double remaining = Math.round(cambioDouble * 100.0) / 100.0;
        for (double d : denoms) {
            int count = (int) Math.floor(remaining / d);
            if (count > 0) {
                String key = d % 1 == 0 ? String.format("%.0f", d) : String.format("%.2f", d);
                desglose.put(key, count);
                remaining = Math.round((remaining - count * d) * 100.0) / 100.0;
            }
        }
        return desglose;
    }


    /** Meses disponibles (agrupa por YearMonth) */
    public List<HistorialMonthDTO> obtenerMesesDisponibles() {
        List<Venta> ventas = ventaRepository.findAll();
        if (ventas.isEmpty()) return Collections.emptyList();

        Map<YearMonth, List<Venta>> porMes = ventas.stream()
                .collect(Collectors.groupingBy(v -> YearMonth.from(v.getFecha().toLocalDate())));

        List<HistorialMonthDTO> meses = porMes.entrySet().stream().map(e -> {
                    YearMonth ym = e.getKey();
                    List<Venta> list = e.getValue();
                    LocalDate start = ym.atDay(1);
                    LocalDate end = ym.atEndOfMonth();

                    BigDecimal totalMes = list.stream()
                            .map(Venta::getTotal)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new HistorialMonthDTO(ym.getYear(), ym.getMonthValue(), start, end, list.size(), totalMes);
                }).sorted(Comparator.comparing(HistorialMonthDTO::getYear).reversed()
                        .thenComparing(HistorialMonthDTO::getMonth).reversed())
                .collect(Collectors.toList());

        return meses;
    }

    /** Semanas dentro de un mes (filtra por año/mes y reutiliza la lógica de settimana) */
    public List<HistorialWeekDTO> obtenerSemanasPorMes(int year, int month) {
        List<Venta> ventas = ventaRepository.findAll();
        WeekFields wf = WeekFields.ISO;

        List<Venta> filtradas = ventas.stream().filter(v -> {
            LocalDate d = v.getFecha().toLocalDate();
            return d.getYear() == year && d.getMonthValue() == month;
        }).collect(Collectors.toList());

        if (filtradas.isEmpty()) return Collections.emptyList();

        Map<String, List<Venta>> porSemana = filtradas.stream().collect(Collectors.groupingBy(v -> {
            LocalDate date = v.getFecha().toLocalDate();
            int week = date.get(wf.weekOfWeekBasedYear());
            int y = date.get(wf.weekBasedYear());
            return y + "-" + week;
        }));

        List<HistorialWeekDTO> semanas = new ArrayList<>();
        for (Map.Entry<String, List<Venta>> e : porSemana.entrySet()) {
            String[] parts = e.getKey().split("-");
            int y = Integer.parseInt(parts[0]);
            int w = Integer.parseInt(parts[1]);
            List<Venta> vlist = e.getValue();

            LocalDate start = LocalDate.now()
                    .withYear(y)
                    .with(wf.weekOfWeekBasedYear(), w)
                    .with(wf.dayOfWeek(), 1);
            LocalDate end = start.with(wf.dayOfWeek(), 7);

            BigDecimal totalSemana = vlist.stream()
                    .map(Venta::getTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            semanas.add(new HistorialWeekDTO(y, w, start, end, vlist.size(), totalSemana));
        }

        semanas.sort(Comparator.comparing(HistorialWeekDTO::getYear).reversed()
                .thenComparing(HistorialWeekDTO::getWeek).reversed());

        return semanas;
    }

    /** Ventas (resumen) de un día particular */
    public List<VentaResumenHistorialDTO> obtenerVentasPorDia(LocalDate date) {
        List<Venta> ventas = ventaRepository.findAll();
        List<Venta> porDia = ventas.stream()
                .filter(v -> v.getFecha().toLocalDate().equals(date))
                .collect(Collectors.toList());

        return porDia.stream().map(v -> {
            VentaResumenHistorialDTO r = new VentaResumenHistorialDTO();
            r.setId(v.getId());
            r.setFecha(v.getFecha());
            r.setTotal(v.getTotal());
            r.setLineas(v.getDetalles() != null ? v.getDetalles().size() : 0);
            return r;
        }).sorted(Comparator.comparing(VentaResumenHistorialDTO::getFecha)).collect(Collectors.toList());
    }

    /**
     * Productos vendidos en un día (agregados).
     * Necesita @Transactional para garantizar carga de productos lazy dentro de la sesión.
     */
    @Transactional(readOnly = true)
    public List<ProductoVendidoDTO> obtenerProductosVendidosPorDia(LocalDate date) {
        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFecha().toLocalDate().equals(date))
                .collect(Collectors.toList());

        Map<Long, ProductoVendidoDTO> mapa = new LinkedHashMap<>();

        for (Venta v : ventas) {
            for (DetalleVenta det : v.getDetalles()) {
                if (det.getProducto() == null) continue;
                Long pid = det.getProducto().getId();
                ProductoVendidoDTO p = mapa.get(pid);

                int cantidad = det.getCantidad() != null ? det.getCantidad() : 0;
                BigDecimal subtotal = det.getSubtotal() != null ? det.getSubtotal() : BigDecimal.ZERO;

                if (p == null) {
                    p = new ProductoVendidoDTO(
                            pid,
                            det.getProducto().getClave(),
                            det.getProducto().getDescripcion(),
                            cantidad,
                            subtotal,
                            1,
                            det.getProducto().getExistencia(),
                            det.getProducto().getExistencia_min()
                    );
                    mapa.put(pid, p);
                } else {
                    p.setCantidadTotal(p.getCantidadTotal() + cantidad);
                    p.setSubtotalTotal(p.getSubtotalTotal().add(subtotal));
                    p.setVeces(p.getVeces() + 1);
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }



    public List<Map<String, Object>> listarVentasDTO() {
        List<Venta> ventas = ventaRepository.findAll();

        return ventas.stream().map(v -> {
            Map<String, Object> ventaDTO = new HashMap<>();
            ventaDTO.put("id", v.getId());
            ventaDTO.put("total", v.getTotal());
            ventaDTO.put("fecha", v.getFecha());

            List<Map<String, Object>> detallesDTO = v.getDetalles().stream().map(det -> {
                Map<String, Object> d = new HashMap<>();

                d.put("id", det.getId());
                d.put("cantidad", det.getCantidad());
                d.put("precio", det.getPrecio());
                d.put("subtotal", det.getSubtotal());

                if (det.getProducto() != null) {
                    d.put("productoId", det.getProducto().getId());
                    d.put("descripcion", det.getProducto().getDescripcion());
                    d.put("clave", det.getProducto().getClave());
                } else {
                    d.put("productoId", null);
                    d.put("descripcion", null);
                    d.put("clave", null);
                }

                return d;
            }).toList();

            ventaDTO.put("detalles", detallesDTO);

            return ventaDTO;
        }).toList();
    }


}
