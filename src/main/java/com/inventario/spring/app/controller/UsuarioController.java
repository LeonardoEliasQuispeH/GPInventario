package com.inventario.spring.app.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 🟦 Listar usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Administrador")) {
            return "redirect:/login";
        }

        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista_usuarios";
    }

    // 🟦 Mostrar formulario nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.getRol().equalsIgnoreCase("Administrador")) {
            return "redirect:/login";
        }

        model.addAttribute("nuevoUsuario", new Usuario());
        return "usuarios/formulario_usuario";
    }

    // 🟩 Guardar usuario nuevo
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("nuevoUsuario") Usuario nuevoUsuario,
                                 HttpSession session, Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !usuarioLogueado.getRol().equalsIgnoreCase("Administrador")) {
            return "redirect:/login";
        }

        // 🟡 Validaciones básicas
        if (nuevoUsuario.getNombre().isEmpty() || nuevoUsuario.getApellido().isEmpty() ||
            nuevoUsuario.getCorreo().isEmpty() || nuevoUsuario.getDni().isEmpty() ||
            nuevoUsuario.getCelular().isEmpty() || nuevoUsuario.getRol().isEmpty()) {
            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "usuarios/formulario_usuario";
        }

        if (!nuevoUsuario.getDni().matches("\\d{8}")) {
            model.addAttribute("error", "El DNI debe tener 8 dígitos.");
            return "usuarios/formulario_usuario";
        }

        if (!nuevoUsuario.getCelular().matches("\\d{9}")) {
            model.addAttribute("error", "El número de celular debe tener 9 dígitos.");
            return "usuarios/formulario_usuario";
        }

        if (!nuevoUsuario.getCorreo().contains("@")) {
            model.addAttribute("error", "Ingrese un correo válido.");
            return "usuarios/formulario_usuario";
        }

        // 🔎 Validar duplicados
        if (usuarioService.existePorCorreo(nuevoUsuario.getCorreo())) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "usuarios/formulario_usuario";
        }

        if (usuarioService.existePorCelular(nuevoUsuario.getCelular())) {
            model.addAttribute("error", "El número de celular ya está registrado.");
            return "usuarios/formulario_usuario";
        }

        if (usuarioService.existePorDni(nuevoUsuario.getDni())) {
            model.addAttribute("error", "El DNI ya está registrado.");
            return "usuarios/formulario_usuario";
        }

        // 🧩 Generar usuario y contraseña automáticos
        String usuarioGenerado = generarUsuario(nuevoUsuario);
        String claveGenerada = generarClave(nuevoUsuario);

        nuevoUsuario.setUsuario(usuarioGenerado);
        nuevoUsuario.setClave(claveGenerada);
        nuevoUsuario.setFechaCreacion(java.time.LocalDateTime.now());

        try {
            usuarioService.guardarUsuario(nuevoUsuario);
            model.addAttribute("mensajeExito",
                "Usuario registrado correctamente. Usuario: " + usuarioGenerado +
                " | Contraseña generada: " + claveGenerada);


            // 🔹 Reiniciar formulario para que quede en blanco
            model.addAttribute("nuevoUsuario", new Usuario());

            return "usuarios/formulario_usuario";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario.");
            return "usuarios/formulario_usuario";
        }
    }

    // 🧮 Generar campo usuario automáticamente
    private String generarUsuario(Usuario u) {
        String inicialApellido = u.getApellido().substring(0, 1).toUpperCase();
        String nombreMinus = u.getNombre().toLowerCase();
        String primerDni = u.getDni().substring(0, 1);
        return inicialApellido + nombreMinus + primerDni;
    }

    // 🧮 Generar contraseña según fórmula HU03
    private String generarClave(Usuario u) {
        String inicialNombre = u.getNombre().substring(0, 1).toUpperCase();
        String tresCelular = u.getCelular().substring(0, 3);
        String tresDni = u.getDni().substring(0, 3);
        String inicialApellido = u.getApellido().substring(0, 1).toUpperCase();
        return inicialNombre + tresCelular + tresDni + inicialApellido;
    }


    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            model.addAttribute("usuario", usuario);
            return "usuarios/editar_usuario";
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            Usuario existente = usuarioService.obtenerUsuarioPorId(usuario.getId());

            // Mantener valores que no se editan
            usuario.setUsuario(existente.getUsuario());
            usuario.setClave(existente.getClave());
            usuario.setFechaCreacion(existente.getFechaCreacion());

            // 🟡 Validaciones básicas
            if (usuario.getNombre().isEmpty() || usuario.getApellido().isEmpty() ||
                usuario.getCorreo().isEmpty() || usuario.getDni().isEmpty() ||
                usuario.getCelular().isEmpty() || usuario.getRol().isEmpty()) {
                
                model.addAttribute("error", "Todos los campos son obligatorios.");
                model.addAttribute("usuario", usuario);
                return "usuarios/editar_usuario";
            }

            if (!usuario.getDni().matches("\\d{8}")) {
                model.addAttribute("error", "El DNI debe tener 8 dígitos.");
                model.addAttribute("usuario", usuario);
                return "usuarios/editar_usuario";
            }

            if (!usuario.getCelular().matches("\\d{9}")) {
                model.addAttribute("error", "El número de celular debe tener 9 dígitos.");
                model.addAttribute("usuario", usuario);
                return "usuarios/editar_usuario";
            }

            if (!usuario.getCorreo().contains("@")) {
                model.addAttribute("error", "Ingrese un correo válido.");
                model.addAttribute("usuario", usuario);
                return "usuarios/editar_usuario";
            }

            // 🟩 Guardar cambios
            usuarioService.actualizarUsuario(usuario);
            model.addAttribute("exito", "Usuario actualizado correctamente.");
            model.addAttribute("usuario", usuario); // volver a pasar el objeto actualizado
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar usuario.");
            model.addAttribute("usuario", usuario);
        }

        return "usuarios/editar_usuario"; // 👈 ya NO redirige
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttrs.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al eliminar usuario.");
        }
        return "redirect:/usuarios";
    }

}
