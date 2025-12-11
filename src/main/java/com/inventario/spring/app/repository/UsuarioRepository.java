package com.inventario.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inventario.spring.app.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Ejemplo: buscar un usuario por su nombre de usuario
    Usuario findByUsuario(String usuario);
    Usuario findByCorreo(String correo);
    Usuario findByCelular(String celular);
    Usuario findByDni(String dni);
}
