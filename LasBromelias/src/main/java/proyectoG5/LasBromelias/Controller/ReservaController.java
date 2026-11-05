package proyectoG5.LasBromelias.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import proyectoG5.LasBromelias.Domain.Cabaña;
import proyectoG5.LasBromelias.Domain.Reserva;
import proyectoG5.LasBromelias.Domain.Usuario;
import proyectoG5.LasBromelias.Service.CabañaService;
import proyectoG5.LasBromelias.Service.ReservaService;
import proyectoG5.LasBromelias.Service.UsuarioService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cliente/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CabañaService cabañaService;
    private final UsuarioService usuarioService;

    //LLenar el formulario con las cabañas disponibles
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cabanas", cabañaService.getCabanasDisponibles());
        return "Reserva/formulario";
    }

    //Cliente crea una nueva reserva
    @PostMapping("/crear")
    public String crearReserva(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam("cabanaId") Long cabanaId,
            @RequestParam("cantidadHuespedes") Integer cantidadHuespedes,
            Principal principal,
            Model model) {

        try {
            Optional<Usuario> resultadoUsuario = usuarioService.getUsuarioPorCorreo(principal.getName());
            if (!resultadoUsuario.isPresent()) {
                throw new IllegalArgumentException("No se encontró el usuario de la sesión.");
            }

            Cabaña cabana = new Cabaña();
            cabana.setId(cabanaId);

            Reserva reserva = new Reserva();
            reserva.setUsuario(resultadoUsuario.get());
            reserva.setCabana(cabana);
            reserva.setFechaEntrada(fechaEntrada);
            reserva.setFechaSalida(fechaSalida);
            reserva.setCantidadHuespedes(cantidadHuespedes);
            reserva.setLlevaMascotas(false);
            reserva.setMoneda("CRC");

            reservaService.crearReserva(reserva);
            return "redirect:/cliente/inicio";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorReserva", e.getMessage());
            model.addAttribute("cabanas", cabañaService.getCabanasDisponibles());
            return "Reserva/formulario";
        }
    }
}
