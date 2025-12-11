package com.inventario.spring.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.spring.app.model.MovimientoProducto;
import com.inventario.spring.app.model.MovimientoProductoMesActual;
import com.inventario.spring.app.model.Producto;
import com.inventario.spring.app.repository.MovimientoProductoRepository;

@Service
public class MovimientoProductoService {

    private final MovimientoProductoRepository movimientoProductoRepository;
    private final ProductoService productoService;

    @Autowired
    private MovimientoProductoRepository repo;

    public MovimientoProductoService(MovimientoProductoRepository movimientoProductoRepository,
                                     ProductoService productoService) {
        this.movimientoProductoRepository = movimientoProductoRepository;
        this.productoService = productoService;
    }

    public List<MovimientoProducto> listarTodos() {
        return movimientoProductoRepository.findAll();
    }

    // 🔹 Registrar entrada
    public void registrarEntrada(MovimientoProducto movimiento) {

        if (movimiento.getCantidadMovimiento() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero.");
        }

        // Buscar producto real en base de datos
        Producto producto = productoService.obtenerProductoPorId(movimiento.getProducto().getId());

        // Actualiza stock
        producto.setStock(producto.getStock() + movimiento.getCantidadMovimiento());
        producto.setEstado("Disponible");

        productoService.actualizarProducto(producto);

        // Registrar movimiento
        movimiento.setTipoMovimiento("entrada");
        movimiento.setFechaMovimiento(LocalDateTime.now());

        movimientoProductoRepository.save(movimiento);
    }

    // Nuevo: registrar salida (valida, actualiza stock y estado, guarda movimiento)
    public void registrarSalida(MovimientoProducto movimiento) {
        if (movimiento.getCantidadMovimiento() <= 0) {
            throw new RuntimeException("La cantidad a retirar debe ser al menos 1.");
        }

        // obtener producto asociado (lanza excepción si no existe)
        Long productoId = movimiento.getProducto() != null ? movimiento.getProducto().getId() : null;
        if (productoId == null) {
            throw new RuntimeException("Debe seleccionar un producto.");
        }

        Producto producto = productoService.obtenerProductoPorId(productoId);

        int cantidadDisponible = producto.getStock();
        int cantidadRetiro = movimiento.getCantidadMovimiento();

        if (cantidadRetiro > cantidadDisponible) {
            throw new RuntimeException("No hay stock suficiente. Disponible: " + cantidadDisponible);
        }

        // actualizar stock y estado
        int nuevoStock = cantidadDisponible - cantidadRetiro;
        producto.setStock(nuevoStock);
        producto.setEstado(nuevoStock == 0 ? "Agotado" : "Disponible");

        // guardar producto (usa tu método de servicio para persistir)
        productoService.actualizarProducto(producto); // asumiendo que devuelve mensaje, pero guarda en BD

        // completar movimiento
        movimiento.setTipoMovimiento("salida");
        movimiento.setFechaMovimiento(LocalDateTime.now());

        // asegurar que la referencia a producto esté completa (puede ser solo id)
        movimiento.setProducto(producto);

        movimientoProductoRepository.save(movimiento);
    }

    public List<MovimientoProducto> movimientosUltimos30Dias() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        return movimientoProductoRepository
                .findByFechaMovimientoBetween(hace30Dias, LocalDateTime.now());
    }

    public List<MovimientoProductoMesActual> obtenerDatosMesActual() {
        return repo.obtenerMovimientosMesActual();
    }

    //HU21 DANIEL
    public List<MovimientoProducto> obtenerHistorialPorProducto(Long idProducto) {
        return movimientoProductoRepository.findByIdProductoOrderByFechaDesc(idProducto);
    }
    //
}