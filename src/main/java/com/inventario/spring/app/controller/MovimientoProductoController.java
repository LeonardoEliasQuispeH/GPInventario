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

    // Mostrar formulario de salida
    @GetMapping("/movimientos/nueva-salida")
    public String mostrarFormularioSalida(Model model) {
        List<Producto> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("movimiento", new MovimientoProducto());
        return "movimientos/formulario_salida";
    }

    // Procesar salida (se queda en la misma vista y muestra mensaje)
    @PostMapping("/movimientos/guardar-salida")
    public String guardarSalida(@ModelAttribute MovimientoProducto movimiento, Model model) {
        try {
            movimientoProductoService.registrarSalida(movimiento);

            model.addAttribute("mensaje", "Salida registrada correctamente.");
            model.addAttribute("tipoMensaje", "success");

            // limpiar formulario
            model.addAttribute("movimiento", new MovimientoProducto());
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            model.addAttribute("tipoMensaje", "danger");

            // mantener los datos del formulario (movimiento) para que usuario corrija
            model.addAttribute("movimiento", movimiento);
        }

        // recargar lista de productos siempre
        model.addAttribute("productos", productoService.listarTodos());

        return "movimientos/formulario_salida";
    }

    @GetMapping("/movimientos/exportar-30-dias")
    public void exportarUltimos30Dias(HttpServletResponse response,
                                    RedirectAttributes redirectAttrs) throws Exception {

        List<MovimientoProducto> movimientos = movimientoProductoService.movimientosUltimos30Dias();

        // Configurar respuesta
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Movimientos_30dias.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Movimientos últimos 30 días");

        // Encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Movimiento");
        header.createCell(1).setCellValue("Producto");
        header.createCell(2).setCellValue("Tipo");
        header.createCell(3).setCellValue("Fecha");
        header.createCell(4).setCellValue("Cantidad");

        // Llenar datos
        int i = 1;
        for (MovimientoProducto m : movimientos) {
            Row fila = sheet.createRow(i++);
            fila.createCell(0).setCellValue(m.getIdMovimiento());
            fila.createCell(1).setCellValue(m.getProducto().getNombreProducto());
            fila.createCell(2).setCellValue(m.getTipoMovimiento());
            fila.createCell(3).setCellValue(m.getFechaMovimiento().toString());
            fila.createCell(4).setCellValue(m.getCantidadMovimiento());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
