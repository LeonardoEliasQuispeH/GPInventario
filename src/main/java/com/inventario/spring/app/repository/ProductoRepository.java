package com.inventario.spring.app.repository;

import com.inventario.spring.app.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 🔍 Permite buscar productos por nombre (para el buscador)
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombreProducto);

}
