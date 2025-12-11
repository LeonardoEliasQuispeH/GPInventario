package com.inventario.spring.app.controller;

import com.inventario.spring.app.model.MovimientoProducto;
import com.inventario.spring.app.model.Producto;
import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.repository.MovimientoProductoRepository;
import com.inventario.spring.app.service.MovimientoProductoService;
import com.inventario.spring.app.service.ProductoService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductoController {

    private final ProductoService productoService;
    private final MovimientoProductoService movimientoProductoService;

    public ProductoController(ProductoService productoService, MovimientoProductoService movimientoProductoService) {
        this.productoService = productoService;
        this.movimientoProductoService = movimientoProductoService;
    }    

    // Mostrar listado general de productos
    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }

        List<Producto> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);
        return "productos/listar_productos";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormularioNuevoProducto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }

        model.addAttribute("producto", new Producto());
        return "productos/formulario_producto"; // nombre del archivo HTML del formulario
    }

    // Guardar producto (y mostrar mensaje en la misma página)
    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }
        
        String mensaje = productoService.guardarProducto(producto);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("producto", new Producto()); // limpia el formulario después
        return "productos/formulario_producto";
    }

    // ✏️ Mostrar formulario de edición
    /*@GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Producto> productoOpt = productoService.buscarPorId(id);

        if (productoOpt.isEmpty()) {
            model.addAttribute("mensaje", "Producto no encontrado.");
            return "redirect:/productos";
        }

        model.addAttribute("producto", productoOpt.get());
        System.out.println("Producto encontrado: " + productoOpt.get());

        return "productos/editar_producto";
    }*/

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }
        
        try {
            Producto producto = productoService.obtenerProductoPorId(id); // Método que lanza excepción si no existe
            model.addAttribute("producto", producto);
        

            return "productos/editar_producto";
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("mensaje", "Producto no encontrado.");
            return "redirect:/productos";
        }
    }

    // 🔄 Actualizar producto existente
    @PostMapping("/productos/actualizar")
    public String actualizarProducto(@ModelAttribute Producto producto, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }
        
        String mensaje = productoService.actualizarProducto(producto);

        // Si hay error → se queda en la misma página
        if (mensaje.startsWith("Error") || mensaje.startsWith("La") || mensaje.startsWith("Todos")) {
            model.addAttribute("mensaje", mensaje);
            model.addAttribute("producto", producto);
            return "productos/editar_producto";
        }

        // Si fue exitoso → redirige al listado con mensaje
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("producto", producto); // Mantener datos visibles
        return "productos/editar_producto";
    }

    // 🔍 Mostrar detalles del producto
    @GetMapping("/productos/detalles/{id}")
    public String mostrarDetallesProducto(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            return "redirect:/login";
        }
        
        try {
            Producto producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            model.addAttribute("producto", producto);

            // ------------------------------------
            // 2️⃣ Obtener historial de movimientos
            // ------------------------------------
            List<MovimientoProducto> historial = 
                    movimientoProductoService.obtenerHistorialPorProducto(id);

            model.addAttribute("historialMovimientos", historial);

            return "productos/detalles_producto";
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("mensaje", "Producto no encontrado.");
            return "redirect:/productos";
        }
    }

    @GetMapping("/productos/exportar-excel")
    public void exportarExcel(HttpSession session, HttpServletResponse response) throws Exception {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Encargado de almacén")) {
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Inventario.xlsx");

        List<Producto> productos = productoService.listarTodos();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario");

        // Encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Cantidad");
        header.createCell(3).setCellValue("Talla");
        header.createCell(4).setCellValue("Color");
        header.createCell(5).setCellValue("Estado");
        header.createCell(6).setCellValue("Fecha Registro");

        int rowIndex = 1;

        for (Producto p : productos) {
            Row fila = sheet.createRow(rowIndex++);
            fila.createCell(0).setCellValue(p.getId());
            fila.createCell(1).setCellValue(p.getNombreProducto());
            fila.createCell(2).setCellValue(p.getStock());
            fila.createCell(3).setCellValue(p.getTalla());
            fila.createCell(4).setCellValue(p.getColor());
            fila.createCell(5).setCellValue(p.getEstado());
            fila.createCell(6).setCellValue(p.getFechaRegistro().toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
