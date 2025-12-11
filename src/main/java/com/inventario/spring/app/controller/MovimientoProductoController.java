package com.inventario.spring.app.controller;


import com.inventario.spring.app.model.MovimientoProducto;
import com.inventario.spring.app.model.Producto;
import com.inventario.spring.app.service.MovimientoProductoService;
import com.inventario.spring.app.service.ProductoService;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MovimientoProductoController {

    private final MovimientoProductoService movimientoProductoService;
    private final ProductoService productoService;  // 🔹 AGREGADO

    // 🔹 AGREGAR ProductoService al constructor
    public MovimientoProductoController(MovimientoProductoService movimientoProductoService,
                                        ProductoService productoService) {
        this.movimientoProductoService = movimientoProductoService;
        this.productoService = productoService;
    }

    

    // 🔹 Listar todos los movimientos de productos
    @GetMapping("/movimientos")
    public String listarMovimientos(Model model) {
        List<MovimientoProducto> movimientos = movimientoProductoService.listarTodos();
        model.addAttribute("movimientos", movimientos);
        return "movimientos/listar_movimientos"; // ruta de la plantilla HTML
    }

    @GetMapping("/movimientos/nueva-entrada")
    public String mostrarFormularioEntrada(Model model) {
        // Obtener todos los productos
        List<Producto> productos = productoService.listarTodos();

        model.addAttribute("productos", productos);
        model.addAttribute("movimiento", new MovimientoProducto());

        return "movimientos/formulario_entrada";
    }

    @PostMapping("/movimientos/guardar-entrada")
    public String guardarEntrada(@ModelAttribute MovimientoProducto movimiento,
                                Model model) {

        try {
            movimientoProductoService.registrarEntrada(movimiento);
            model.addAttribute("mensaje", "Entrada registrada correctamente.");
            model.addAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            model.addAttribute("tipoMensaje", "danger");
        }

        // Volver a cargar los productos
        model.addAttribute("productos", productoService.listarTodos());

        // Devolver un nuevo objeto para limpiar el formulario
        model.addAttribute("movimiento", new MovimientoProducto());

        return "movimientos/formulario_entrada";
    }

    

}
