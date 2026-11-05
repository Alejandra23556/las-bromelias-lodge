package proyectoG5.LasBromelias.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import proyectoG5.LasBromelias.Service.CabañaService;

@Controller
@RequiredArgsConstructor
public class IndexController {

    //Controller de las paginas de inicio (para Visitantes)

    private final CabañaService cabañaService;

    @GetMapping("/")
    public String inicio(Model model) {
        return "index";
    }

    @GetMapping("/cabanas")
    public String mostrarCabanas(Model model) {
        model.addAttribute("cabanas", cabañaService.getCabanasDisponibles());
        return "Visitante/VisitaCabaña";
    }
}
