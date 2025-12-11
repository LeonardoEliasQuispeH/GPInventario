package com.inventario.spring.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String clave,
                                HttpSession session,
                                Model model) {

        // Validación de campos vacíos
        if (usuario == null || usuario.isEmpty()) {
            model.addAttribute("error", "Usuario vacío");
            return "login";
        }

        if (clave == null || clave.isEmpty()) {
            model.addAttribute("error", "Contraseña vacía");
            return "login";
        }

        // Verificar credenciales
        Usuario usuarioEncontrado = usuarioService.verificarCredenciales(usuario, clave);

        if (usuarioEncontrado != null) {
            // Guardar usuario en sesión
            session.setAttribute("usuarioLogueado", usuarioEncontrado);

            // Redirigir según rol (exactamente como en las HU)
            String rol = usuarioEncontrado.getRol();

            switch (rol) {
                case "Administrador":
                    return "redirect:/usuarios"; // muestra listado de usuarios
                case "Encargado de almacén":
                    return "redirect:/productos";
                case "Gerente":
                    return "redirect:/reporte/graficos";
            }
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        return "login"; // si falla el login, vuelve al formulario
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // elimina la sesión
        return "redirect:/login";
    }
}

