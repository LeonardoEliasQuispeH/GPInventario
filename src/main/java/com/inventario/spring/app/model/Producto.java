package com.inventario.spring.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Código de producto generado automáticamente

    @Column(nullable = false)
    private String nombreProducto; // Nombre del producto

    @Column(nullable = false)
    private int stock; // Cantidad disponible en inventario

    @Column(nullable = false)
    private String talla; // Talla del producto (S, M, L, XL, etc.)

    @Column(nullable = false)
    private String color; // Color del producto

    @Column(nullable = false)
    private String estado; // Disponible / Agotado

    @Column(nullable = false)
    private LocalDateTime fechaRegistro; // Fecha de registro automática

    // 🔹 Constructor vacío
    public Producto() {}

    // 🔹 Constructor con parámetros
    public Producto(String nombreProducto, int stock, String talla, String color, String estado, LocalDateTime fechaRegistro) {
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.talla = talla;
        this.color = color;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // 🔹 Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
