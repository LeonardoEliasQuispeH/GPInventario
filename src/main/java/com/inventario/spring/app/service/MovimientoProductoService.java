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

   
    public List<MovimientoProductoMesActual> obtenerDatosMesActual() {
        return repo.obtenerMovimientosMesActual();
    }

    //HU21 DANIEL
    public List<MovimientoProducto> obtenerHistorialPorProducto(Long idProducto) {
        return movimientoProductoRepository.findByIdProductoOrderByFechaDesc(idProducto);
    }
    //
}
