package com.inventario.spring.app.service;

import com.inventario.spring.app.model.Producto;
import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return listarTodos();
        }
        return productoRepository.findByNombreProductoContainingIgnoreCase(nombre);
    }

    public String guardarProducto(Producto producto) {
        // Validación: todos los campos obligatorios
        if (producto.getNombreProducto() == null || producto.getNombreProducto().isEmpty() ||
            producto.getTalla() == null || producto.getTalla().isEmpty() ||
            producto.getColor() == null || producto.getColor().isEmpty()) {
            return "Todos los campos son obligatorios.";
        }

        // Validación: cantidad no negativa
        if (producto.getStock() < 0) {
            return "La cantidad no puede ser negativa.";
        }

        // Estado automático
        producto.setEstado(producto.getStock() > 0 ? "Disponible" : "Agotado");

        // Asignar fecha de registro automática si es nuevo producto
        if (producto.getFechaRegistro() == null) {
            producto.setFechaRegistro(LocalDateTime.now());
        }

        try {
            productoRepository.save(producto);
            return "Producto registrado correctamente.";
        } catch (Exception e) {
            return "Error al registrar producto.";
        }

    }

    public String actualizarProducto(Producto producto) {
        if (producto.getNombreProducto() == null || producto.getNombreProducto().isEmpty() ||
            producto.getTalla() == null || producto.getTalla().isEmpty() ||
            producto.getColor() == null || producto.getColor().isEmpty()) {
            return "Todos los campos son obligatorios.";
        }

        if (producto.getStock() < 0) {
            return "La cantidad no puede ser negativa.";
        }

        Optional<Producto> productoExistente = productoRepository.findById(producto.getId());
        if (productoExistente.isEmpty()) {
            return "Error al actualizar producto.";
        }

        Producto actual = productoExistente.get();

        // Solo actualizar los campos editables
        actual.setNombreProducto(producto.getNombreProducto());
        actual.setStock(producto.getStock());
        actual.setTalla(producto.getTalla());
        actual.setColor(producto.getColor());
        actual.setEstado(actual.getStock() > 0 ? "Disponible" : "Agotado");

        try {
            productoRepository.save(actual);
            return "Producto actualizado correctamente.";
        } catch (Exception e) {
            return "Error al actualizar producto.";
        }
    }
}
