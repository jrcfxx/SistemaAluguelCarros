package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {

    private final SessionAuthService sessionAuthService;
    private final AgenteSessionService agenteSessionService;

    public HomeController(
            SessionAuthService sessionAuthService,
            AgenteSessionService agenteSessionService
    ) {
        this.sessionAuthService = sessionAuthService;
        this.agenteSessionService = agenteSessionService;
    }

    @Get("/")
    public ModelAndView<Map<String, Object>> index(@Nullable Session session) {
        Map<String, Object> model = new LinkedHashMap<>();
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        Optional<AgenteSessao> agenteAutenticado = agenteSessionService.agenteAutenticado(session);
        model.put("autenticado", clienteAutenticado.isPresent());
        model.put("clienteNome", clienteAutenticado.map(Cliente::getNome).orElse(null));
        model.put("clienteId", clienteAutenticado.map(Cliente::getId).orElse(null));
        model.put("agenteAutenticado", agenteAutenticado.isPresent());
        model.put("agenteNome", agenteAutenticado.map(AgenteSessao::nomeExibicao).orElse(null));
        return new ModelAndView<>("home", model);
    }
}
