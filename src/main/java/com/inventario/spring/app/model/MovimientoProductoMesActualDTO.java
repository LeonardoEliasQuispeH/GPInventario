package com.inventario.spring.app.model;

public class MovimientoProductoMesActualDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidadTotal;
    private Integer mes;
    private Integer anio;
    private String mesNombre;

    public MovimientoProductoMesActualDTO() {}

    public MovimientoProductoMesActualDTO(Long idProducto, String nombreProducto, Integer cantidadTotal, Integer mes, Integer anio, String mesNombre) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidadTotal = cantidadTotal;
        this.mes = mes;
        this.anio = anio;
        this.mesNombre = mesNombre;
    }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Integer getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(Integer cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getMesNombre() { return mesNombre; }
    public void setMesNombre(String mesNombre) { this.mesNombre = mesNombre; }
}
