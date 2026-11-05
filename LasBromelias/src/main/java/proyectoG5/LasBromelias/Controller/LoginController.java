package proyectoG5.LasBromelias.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyectoG5.LasBromelias.Domain.Usuario;
import proyectoG5.LasBromelias.Service.UsuarioService;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class LoginController {

    private final UsuarioService usuarioService;

    //Al utilizar Spting Security, el login se realiza fuera del controller. Consultar
    //security.xml

    //Metodo para mostrar mensajes en el login
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        model.addAttribute("usuarioLogin", new Usuario());

        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "La sesión se cerró correctamente.");
        }

        return "Login/login";
    }

    @GetMapping("/registro/nuevo")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuarioRegistro", new Usuario());
        return "Login/registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.save(usuario);
            return "redirect:/login?registro";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registroError", e.getMessage());
            model.addAttribute("usuarioRegistro", new Usuario());
            return "Login/registro";
        }
    }
}
