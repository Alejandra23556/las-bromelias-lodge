package proyectoG5.LasBromelias.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import proyectoG5.LasBromelias.Domain.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    //Obtener las reservas de un usuario ordenadas de la más reciente a la más antigua
    List<Reserva> findByUsuario_CorreoOrderByFechaCreacionDesc(String correo);

    //Filtrar reservas segun estado ordenadas por fecha
    List<Reserva> findByEstadoOrderByFechaEntradaAsc(String estado);

    //Comprobar que una fecha este disponible
    boolean existsByCabana_IdAndEstadoInAndFechaEntradaLessThanAndFechaSalidaGreaterThan(Long cabanaId, Collection<String> estados, LocalDate fechaSalida, LocalDate fechaEntrada);
}
