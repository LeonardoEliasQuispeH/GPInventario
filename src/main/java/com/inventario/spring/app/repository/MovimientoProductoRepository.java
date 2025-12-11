package com.inventario.spring.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventario.spring.app.model.MovimientoProducto;
import com.inventario.spring.app.model.MovimientoProductoMesActual;

@Repository
public interface MovimientoProductoRepository extends JpaRepository<MovimientoProducto, Long> {

    List<MovimientoProducto> findByFechaMovimientoBetween(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
    );

    List<MovimientoProducto> findByTipoMovimiento(String tipoMovimiento);

    
    //HU DANIEL
    @Query("SELECT m FROM MovimientoProducto m WHERE m.producto.id = :idProducto ORDER BY m.fechaMovimiento DESC")
    List<MovimientoProducto> findByIdProductoOrderByFechaDesc(@Param("idProducto") Long idProducto);

    
}
