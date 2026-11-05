package proyectoG5.LasBromelias.Controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import proyectoG5.LasBromelias.Service.ReservaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cliente")
public class ClienteController {

    private final ReservaService reservaService;

    @GetMapping("/inicio")
    public String inicio(Principal principal, Model model) {
        model.addAttribute("reservas", reservaService.getReservasPorUsuario(principal.getName()));
        return "Cliente/inicio";
    }
}
