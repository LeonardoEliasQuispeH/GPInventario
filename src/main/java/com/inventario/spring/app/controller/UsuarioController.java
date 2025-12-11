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
import org.springframework.web.bind.annotation.RequestParam;
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

   

    @PostMapping("/usuarios/cambiar-estado/{id}")
    public String cambiarEstadoUsuario(@PathVariable Long id, @RequestParam("activo") boolean activo,
                                    RedirectAttributes redirectAttrs) {
        try {
            usuarioService.cambiarEstadoUsuario(id, activo);
            String mensaje = activo ? "Usuario activado correctamente." : "Usuario desactivado correctamente.";
            redirectAttrs.addFlashAttribute("exito", mensaje);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el estado del usuario.");
        }
        return "redirect:/usuarios";
    }

}
