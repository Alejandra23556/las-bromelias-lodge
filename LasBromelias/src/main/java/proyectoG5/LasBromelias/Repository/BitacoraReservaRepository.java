package proyectoG5.LasBromelias.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import proyectoG5.LasBromelias.Domain.BitacoraReserva;

public interface BitacoraReservaRepository extends JpaRepository<BitacoraReserva, Long> {

    List<BitacoraReserva> findByReserva_IdOrderByFechaHoraDesc(Long reservaId);
}
