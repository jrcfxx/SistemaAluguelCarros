package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.AutomovelService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/catalogo")
public class CatalogoController {

    private final AutomovelService automovelService;
    private final SessionAuthService sessionAuthService;
    private final AgenteSessionService agenteSessionService;

    public CatalogoController(
            AutomovelService automovelService,
            SessionAuthService sessionAuthService,
            AgenteSessionService agenteSessionService
    ) {
        this.automovelService = automovelService;
        this.sessionAuthService = sessionAuthService;
        this.agenteSessionService = agenteSessionService;
    }

    @Get
    public ModelAndView<Map<String, Object>> catalogo(
            @Nullable Session session,
            @Nullable @QueryValue String q,
            @Nullable @QueryValue Integer anoMin,
            @Nullable @QueryValue Integer anoMax,
            @Nullable @QueryValue String sort
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        Optional<AgenteSessao> agenteAutenticado = agenteSessionService.agenteAutenticado(session);

        List<Automovel> resultados = automovelService.buscarCatalogo(q, anoMin, anoMax, sort);

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("autenticado", clienteAutenticado.isPresent());
        model.put("clienteNome", clienteAutenticado.map(Cliente::getNome).orElse(null));
        model.put("clienteId", clienteAutenticado.map(Cliente::getId).orElse(null));
        model.put("agenteAutenticado", agenteAutenticado.isPresent());
        model.put("agenteNome", agenteAutenticado.map(AgenteSessao::nomeExibicao).orElse(null));

        model.put("q", q == null ? "" : q.trim());
        model.put("anoMin", anoMin);
        model.put("anoMax", anoMax);
        model.put("sort", sort == null ? "relevancia" : sort);
        model.put("automoveis", resultados);
        model.put("total", resultados.size());
        return new ModelAndView<>("catalogo", model);
    }
}

