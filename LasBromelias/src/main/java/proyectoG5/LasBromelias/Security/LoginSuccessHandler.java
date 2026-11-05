package proyectoG5.LasBromelias.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

//Clase para manejar la seguridad y la separacion por roles del sistema

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        for (GrantedAuthority autoridad : authentication.getAuthorities()) {
            if ("ROLE_CLIENT".equals(autoridad.getAuthority())) {
                return request.getContextPath() + "/cliente/inicio";
            }
        }
        return super.determineTargetUrl(request, response, authentication);
    }
}
