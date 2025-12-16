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

    @Query(value = """
        SELECT 
            p.id AS idProducto,
            p.nombre_producto AS nombreProducto,
            SUM(m.cantidad_movimiento) AS cantidadTotal,
            MONTH(m.fecha_movimiento) AS mes,
            YEAR(m.fecha_movimiento) AS anio
        FROM movimiento_producto m
        INNER JOIN producto p ON p.id = m.id_producto
        WHERE MONTH(m.fecha_movimiento) = MONTH(CURRENT_DATE())
          AND YEAR(m.fecha_movimiento) = YEAR(CURRENT_DATE())
          AND m.tipo_movimiento = 'salida'
        GROUP BY p.id, p.nombre_producto, MONTH(m.fecha_movimiento), YEAR(m.fecha_movimiento)
        ORDER BY p.nombre_producto
        """, nativeQuery = true)
    List<MovimientoProductoMesActual> obtenerMovimientosMesActual();

    @Query(value = """
        SELECT 
            p.id AS idProducto,
            p.nombre_producto AS nombreProducto,
            SUM(m.cantidad_movimiento) AS cantidadTotal,
            MONTH(m.fecha_movimiento) AS mes,
            YEAR(m.fecha_movimiento) AS anio
        FROM movimiento_producto m
        INNER JOIN producto p ON p.id = m.id_producto
        WHERE m.tipo_movimiento = 'salida'
          AND m.fecha_movimiento BETWEEN :inicio AND :fin
        GROUP BY p.id, p.nombre_producto, MONTH(m.fecha_movimiento), YEAR(m.fecha_movimiento)
        ORDER BY YEAR(m.fecha_movimiento), MONTH(m.fecha_movimiento), p.nombre_producto
        """, nativeQuery = true)
    List<MovimientoProductoMesActual> obtenerMovimientosPorRango(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    //HU DANIEL
    @Query("SELECT m FROM MovimientoProducto m WHERE m.producto.id = :idProducto ORDER BY m.fechaMovimiento DESC")
    List<MovimientoProducto> findByIdProductoOrderByFechaDesc(@Param("idProducto") Long idProducto);

    //ENTRADAS DEL MES ACTUAL
    @Query(value = """
    SELECT 
        p.id AS idProducto,
        p.nombre_producto AS nombreProducto,
        SUM(m.cantidad_movimiento) AS cantidadTotal,
        MONTH(m.fecha_movimiento) AS mes,
        YEAR(m.fecha_movimiento) AS anio
    FROM movimiento_producto m
    INNER JOIN producto p ON p.id = m.id_producto
    WHERE MONTH(m.fecha_movimiento) = MONTH(CURRENT_DATE())
      AND YEAR(m.fecha_movimiento) = YEAR(CURRENT_DATE())
      AND m.tipo_movimiento = 'entrada'
    GROUP BY p.id, p.nombre_producto, mes, anio
    """, nativeQuery = true)
    List<MovimientoProductoMesActual> obtenerEntradasMesActual();

   
    
}
