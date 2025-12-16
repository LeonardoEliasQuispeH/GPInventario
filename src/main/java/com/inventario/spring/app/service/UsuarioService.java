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

        if (user == null) {
            return null;
        }

        // Verifica contraseña
        if (!passwordEncoder.matches(contrasena, user.getClave())) {
            return null;
        }

        // 🔒 Verifica estado
        if (!"Activo".equalsIgnoreCase(user.getEstado())) {
            throw new RuntimeException("USUARIO_DESACTIVADO");
        }

        return user;
    }

    /**
     * Retorna la lista completa de usuarios registrados en el sistema.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

  /**
     * Guarda un usuario nuevo en la base de datos.
     * Antes de guardar, aplica el hash a la contraseña generada automáticamente.
     */
    public void guardarUsuario(Usuario usuario) {
        // Aplica hash BCrypt a la contraseña generada automáticamente
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Guarda el nuevo usuario con la contraseña encriptada
        usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     * No se modifica la contraseña ni el nombre de usuario (campo 'usuario').
     */
    public void actualizarUsuario(Usuario usuario) {
        // Busca el usuario existente por ID o lanza una excepción si no existe
        Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Actualiza solo los campos editables
        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setCorreo(usuario.getCorreo());
        existente.setDni(usuario.getDni());
        existente.setCelular(usuario.getCelular());
        existente.setRol(usuario.getRol());

        // Guarda los cambios en la base de datos
        usuarioRepository.save(existente);
    }

    /**
     * Obtiene un usuario específico por su ID.
     * Lanza una excepción si no se encuentra el registro.
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    /**
     * Verifica si ya existe un usuario con el mismo correo.
     */
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo) != null;
    }

    /**
     * Verifica si ya existe un usuario con el mismo número de celular.
     */
    public boolean existePorCelular(String celular) {
        return usuarioRepository.findByCelular(celular) != null;
    }

    /**
     * Verifica si ya existe un usuario con el mismo DNI.
     */
    public boolean existePorDni(String dni) {
        return usuarioRepository.findByDni(dni) != null;
    }
  

  
  
  


    public void cambiarEstadoUsuario(Long id, boolean activo) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    usuario.setEstado(activo ? "Activo" : "Desactivado");
    usuarioRepository.save(usuario);
    }
}
