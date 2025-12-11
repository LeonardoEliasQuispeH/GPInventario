package com.inventario.spring.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.spring.app.model.MovimientoProductoMesActualDTO;
import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReporteController {

    private final ReporteService reporteService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // ---------- Método 1: mes actual (al cargar la página) ----------
    @GetMapping("/reporte/graficos")
    public String mostrarGraficos(HttpSession session, Model model) throws JsonProcessingException {
        // validación de sesión/rol (igual que tenías)
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";
        String rol = usuario.getRol();
        if (!rol.equalsIgnoreCase("Administrador") && !rol.equalsIgnoreCase("Gerente")) {
            return "redirect:/login";
        }

        List<MovimientoProductoMesActualDTO> tabla = reporteService.obtenerSalidasMesActual();

        // totales por mes
        Map<String,Integer> totales = reporteService.calcularTotalesPorMes(tabla);
        List<String> labels = new ArrayList<>(totales.keySet()); // "YYYY-MM"
        List<Integer> values = labels.stream().map(totales::get).collect(Collectors.toList());
        // convertir labels a nombres de mes para mostrar en X
        List<String> labelsMesNombre = labels.stream()
                .map(l -> {
                    String[] parts = l.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    return monthName(month) + " " + year;
                }).collect(Collectors.toList());

        model.addAttribute("datos", tabla);
        model.addAttribute("datosJsonTable", mapper.writeValueAsString(tabla)); // tabla por producto (para debug si quieres)
        // pasar JSON para gráfico: { labels: [...], values:[...] }
        Map<String,Object> graf = new HashMap<>();
        graf.put("labels", labelsMesNombre);
        graf.put("values", values);
        model.addAttribute("datosJsonMonths", mapper.writeValueAsString(graf));

        // mantener valores del filtro vacío
        model.addAttribute("mesInicio", "");
        model.addAttribute("mesFin", "");

        return "reportes/graficos";
    }

    // ---------- Método 2: rango por meses (formulario: mesInicio, mesFin tipo YYYY-MM) ----------
    @GetMapping("/reporte/graficos/rango")
    public String mostrarPorRango(
            @RequestParam("mesInicio") String mesInicio,
            @RequestParam("mesFin") String mesFin,
            HttpSession session,
            Model model) throws Exception {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";
        String rol = usuario.getRol();
        if (!rol.equalsIgnoreCase("Administrador") && !rol.equalsIgnoreCase("Gerente")) {
            return "redirect:/login";
        }

        // mesInicio y mesFin con formato "YYYY-MM" (input type="month")
        LocalDate inicio = LocalDate.parse(mesInicio + "-01");
        LocalDate fin = LocalDate.parse(mesFin + "-01").withDayOfMonth(
                LocalDate.parse(mesFin + "-01").lengthOfMonth()
        );

        List<MovimientoProductoMesActualDTO> tabla = reporteService.obtenerRangoMeses(inicio, fin);

        // construir totales por mes
        Map<String,Integer> totales = reporteService.calcularTotalesPorMes(tabla);
        List<String> labels = new ArrayList<>(totales.keySet());
        List<Integer> values = labels.stream().map(totales::get).collect(Collectors.toList());
        List<String> labelsMesNombre = labels.stream()
                .map(l -> {
                    String[] parts = l.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    return monthName(month) + " " + year;
                }).collect(Collectors.toList());

        model.addAttribute("datos", tabla);
        model.addAttribute("datosJsonTable", mapper.writeValueAsString(tabla));
        Map<String,Object> graf = new HashMap<>();
        graf.put("labels", labelsMesNombre);
        graf.put("values", values);
        model.addAttribute("datosJsonMonths", mapper.writeValueAsString(graf));

        // mantener valores del filtro en la vista
        model.addAttribute("mesInicio", mesInicio);
        model.addAttribute("mesFin", mesFin);

        return "reportes/graficos";
    }

    private String monthName(int m) {
        return new String[]{
                "Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        }[m - 1];
    }
}

