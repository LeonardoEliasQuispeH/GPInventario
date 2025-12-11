package com.inventario.spring.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventario.spring.app.model.Usuario;
import com.inventario.spring.app.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Instancia de BCrypt para aplicar hash seguro a las contraseñas
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Verifica las credenciales del usuario en el login.
     * Compara la contraseña ingresada con el hash almacenado en la base de datos.
     */
    public Usuario verificarCredenciales(String usuario, String contrasena) {
        Usuario user = usuarioRepository.findByUsuario(usuario);

        if (user != null && passwordEncoder.matches(contrasena, user.getClave())) {
            return user; // Credenciales válidas
        }

        return null; // Credenciales inválidas
    }

    /**
     * Retorna la lista completa de usuarios registrados en el sistema.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

  


    public void cambiarEstadoUsuario(Long id, boolean activo) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    usuario.setEstado(activo ? "Activo" : "Desactivado");
    usuarioRepository.save(usuario);
    }
}
