package proyectoG5.LasBromelias.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyectoG5.LasBromelias.Domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}


