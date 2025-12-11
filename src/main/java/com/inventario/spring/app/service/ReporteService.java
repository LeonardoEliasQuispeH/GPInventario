package com.inventario.spring.app.service;

import com.inventario.spring.app.model.MovimientoProductoMesActual;
import com.inventario.spring.app.model.MovimientoProductoMesActualDTO;
import com.inventario.spring.app.repository.MovimientoProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final MovimientoProductoRepository repo;

    public ReporteService(MovimientoProductoRepository repo) {
        this.repo = repo;
    }

    private String nombreMes(int m) {
        return new String[]{
                "Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        }[m - 1];
    }

    // trae datos por producto para el mes actual (tabla)
    public List<MovimientoProductoMesActualDTO> obtenerSalidasMesActual() {
        List<MovimientoProductoMesActual> raw = repo.obtenerMovimientosMesActual();
        return raw.stream()
                .map(r -> new MovimientoProductoMesActualDTO(
                        r.getIdProducto(),
                        r.getNombreProducto(),
                        r.getCantidadTotal(),
                        r.getMes(),
                        r.getAnio(),
                        nombreMes(r.getMes())
                ))
                .collect(Collectors.toList());
    }

    // trae datos por producto para un rango de fechas (tabla)
    public List<MovimientoProductoMesActualDTO> obtenerRangoMeses(LocalDate inicioDate, LocalDate finDate) {
        LocalDateTime inicio = inicioDate.atStartOfDay();
        LocalDateTime fin = finDate.atTime(23,59,59);
        List<MovimientoProductoMesActual> raw = repo.obtenerMovimientosPorRango(inicio, fin);
        return raw.stream()
                .map(r -> new MovimientoProductoMesActualDTO(
                        r.getIdProducto(),
                        r.getNombreProducto(),
                        r.getCantidadTotal(),
                        r.getMes(),
                        r.getAnio(),
                        nombreMes(r.getMes())
                ))
                .collect(Collectors.toList());
    }

    // calcula totales por mes (para el gráfico) a partir de la lista DTO por producto
    public Map<String, Integer> calcularTotalesPorMes(List<MovimientoProductoMesActualDTO> lista) {
        // clave: "YYYY-MM", valor: suma de cantidades
        Map<String, Integer> map = new LinkedHashMap<>();
        // ordenar por anio, mes
        lista.stream()
            .sorted(Comparator.comparing(MovimientoProductoMesActualDTO::getAnio)
                    .thenComparing(MovimientoProductoMesActualDTO::getMes))
            .forEach(d -> {
                String key = d.getAnio() + "-" + String.format("%02d", d.getMes());
                map.put(key, map.getOrDefault(key, 0) + (d.getCantidadTotal() == null ? 0 : d.getCantidadTotal()));
            });
        return map;
    }
}
