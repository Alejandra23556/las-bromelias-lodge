package proyectoG5.LasBromelias.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyectoG5.LasBromelias.Domain.Usuario;
import proyectoG5.LasBromelias.Repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    //Seguridad de Springboot para contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //Buscar Usuarios por su correo
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        if (correo == null || correo.trim().isEmpty()) {
            throw new UsernameNotFoundException("Debe indicar el correo.");
        }

        Optional<Usuario> resultado = usuarioRepository.findByCorreo(correo.trim().toLowerCase());
        if (!resultado.isPresent()) {
            throw new UsernameNotFoundException("No existe una cuenta con ese correo.");
        }

        Usuario usuario = resultado.get();

        //Spring Security
        return org.springframework.security.core.userdetails.User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .authorities(usuario.getRol())
                .disabled(!usuario.getActivo())
                .build();
    }

    //Traer todos los usuarios
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    //Traer solo un usuario
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    //Guardar o actualizar datos de usuario
    //Inicialmente todas las cuentas son cliente, las cuentas de Staff se crean de forma separada por
    //motivos de seguridad
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Debe indicar los datos del usuario.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }

        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());

        //Validacion de datos
        if (usuario.getId() == null) {
            if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
                throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
            }
            if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }

            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            usuario.setRol("ROLE_CLIENT");
            usuario.setActivo(true);
            usuario.setFechaRegistro(LocalDateTime.now());
        } else {
            Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());
            if (!resultado.isPresent()) {
                throw new IllegalArgumentException("El usuario no existe.");
            }
            Usuario usuarioActual = resultado.get();

            if (usuarioRepository.existsByCorreoAndIdNot(usuario.getCorreo(), usuario.getId())) {
                throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
            }

            if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
                usuario.setContrasena(usuarioActual.getContrasena());
            } else {
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            }

            if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
                usuario.setRol(usuarioActual.getRol());
            }
            if (usuario.getActivo() == null) {
                usuario.setActivo(usuarioActual.getActivo());
            }
            usuario.setFechaRegistro(usuarioActual.getFechaRegistro());
        }

        return usuarioRepository.save(usuario);
    }

    //Eliminacion de usuario (el usuario solo pasa a ser inactivo)
    @Transactional
    public void delete(Long idUsuario) {
        Optional<Usuario> resultado = usuarioRepository.findById(idUsuario);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }

        Usuario usuario = resultado.get();
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}

