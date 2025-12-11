package com.inventario.spring.app.model;

public interface MovimientoProductoMesActual {
    Long getIdProducto();
    String getNombreProducto();
    Integer getCantidadTotal();
    Integer getMes();
    Integer getAnio();
}
