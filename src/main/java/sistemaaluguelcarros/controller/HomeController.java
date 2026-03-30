package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {

    private final SessionAuthService sessionAuthService;

    public HomeController(SessionAuthService sessionAuthService) {
        this.sessionAuthService = sessionAuthService;
    }

    @Get("/")
    public ModelAndView<Map<String, Object>> index(@Nullable Session session) {
        Map<String, Object> model = new LinkedHashMap<>();
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        model.put("autenticado", clienteAutenticado.isPresent());
        model.put("clienteNome", clienteAutenticado.map(Cliente::getNome).orElse(null));
        return new ModelAndView<>("home", model);
    }
}
