package com.tlapaleria.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pago_con = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal cambio = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal cargo_extra = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
    }

    public BigDecimal getTotal() { return total != null ? total : BigDecimal.ZERO; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getPago_con() { return pago_con != null ? pago_con : BigDecimal.ZERO; }
    public void setPago_con(BigDecimal pago_con) { this.pago_con = pago_con; }

    public BigDecimal getCambio() { return cambio != null ? cambio : BigDecimal.ZERO; }
    public void setCambio(BigDecimal cambio) { this.cambio = cambio; }

    public BigDecimal getCargo_extra() { return cargo_extra != null ? cargo_extra : BigDecimal.ZERO; }
    public void setCargo_extra(BigDecimal cargo_extra) { this.cargo_extra = cargo_extra; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
