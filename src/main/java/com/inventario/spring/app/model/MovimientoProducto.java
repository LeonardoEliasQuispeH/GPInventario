package com.inventario.spring.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MovimientoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    // 🔹 Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY) // carga diferida para optimizar
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto; // relación directa con la entidad Producto

    @Column(nullable = false)
    private String tipoMovimiento; // 'entrada' o 'salida'

    @Column(nullable = false)
    private int cantidadMovimiento;

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    // 🔹 Constructor vacío
    public MovimientoProducto() {}

    // 🔹 Constructor con parámetros
    public MovimientoProducto(Producto producto, String tipoMovimiento, int cantidadMovimiento, LocalDateTime fechaMovimiento) {
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadMovimiento = cantidadMovimiento;
        this.fechaMovimiento = fechaMovimiento;
    }

    // 🔹 Getters y setters
    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidadMovimiento() {
        return cantidadMovimiento;
    }

    public void setCantidadMovimiento(int cantidadMovimiento) {
        this.cantidadMovimiento = cantidadMovimiento;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
