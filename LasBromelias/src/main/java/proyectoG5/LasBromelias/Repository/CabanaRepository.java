package proyectoG5.LasBromelias.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyectoG5.LasBromelias.Domain.Cabaña;
import java.util.List;

public interface CabanaRepository extends JpaRepository<Cabaña, Long> {

    List<Cabaña> findByEstadoIgnoreCase(String estado);
}


