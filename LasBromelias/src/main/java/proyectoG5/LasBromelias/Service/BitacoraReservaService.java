package proyectoG5.LasBromelias.Service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyectoG5.LasBromelias.Domain.BitacoraReserva;
import proyectoG5.LasBromelias.Domain.Reserva;
import proyectoG5.LasBromelias.Domain.Usuario;
import proyectoG5.LasBromelias.Repository.BitacoraReservaRepository;

@Service
@RequiredArgsConstructor
public class BitacoraReservaService {

    private final BitacoraReservaRepository bitacoraReservaRepository;

    //Metodo para obtener Bitacoras y rellenar modelo
    @Transactional(readOnly = true)
    public List<BitacoraReserva> getBitacoraReserva(Long idReserva) {
        if (idReserva == null || idReserva <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser válido.");
        }
        return bitacoraReservaRepository.findByReserva_IdOrderByFechaHoraDesc(idReserva);
    }

    //Metodo para actualizar o guardar(en caso de que no exista) una bitacora nueva en el panel de bitacoras.
    @Transactional
    public BitacoraReserva registrarCambioEstado(Reserva reserva, String estadoAnterior, String estadoNuevo, Usuario usuario) {
        if (reserva == null || reserva.getId() == null) {
            throw new IllegalArgumentException("La reserva debe existir antes de registrar el cambio.");
        }
        if (estadoNuevo == null || estadoNuevo.isBlank()) {
            throw new IllegalArgumentException("El estado nuevo es obligatorio.");
        }

        BitacoraReserva registro = new BitacoraReserva();
        registro.setReserva(reserva);
        registro.setEstadoAnterior(estadoAnterior);
        registro.setEstadoNuevo(estadoNuevo);
        registro.setUsuario(usuario);
        registro.setFechaHora(LocalDateTime.now());

        BitacoraReserva guardado = bitacoraReservaRepository.save(registro);
        return guardado;
    }
}



