package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.AutomovelService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/agente/automoveis")
public class AutomovelController {

    private static final URI LOGIN_URI = URI.create("/agente/login");

    private final AgenteSessionService agenteSessionService;
    private final AutomovelService automovelService;

    public AutomovelController(AgenteSessionService agenteSessionService, AutomovelService automovelService) {
        this.agenteSessionService = agenteSessionService;
        this.automovelService = automovelService;
    }

    @Get
    public Object listar(
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        List<Automovel> lista = automovelService.listarTodos();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.get().nomeExibicao());
        model.put("automoveis", lista);
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        return new ModelAndView<>("agente/automoveis/lista", model);
    }

    @Get("/novo")
    public Object novo(@Nullable Session session, @Nullable @QueryValue String erro) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        return formulario(agente.get(), null, erro);
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object criar(
            @Nullable Session session,
            String placa,
            String marca,
            String modelo,
            Integer ano,
            @Nullable String fotoUrl
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        try {
            automovelService.cadastrar(placa, marca, modelo, ano, fotoUrl);
            URI uri = UriBuilder.of("/agente/automoveis")
                    .queryParam("mensagem", "Automóvel cadastrado com sucesso.")
                    .build();
            return HttpResponse.seeOther(uri);
        } catch (IllegalStateException ex) {
            return formulario(agente.get(), preencherForm(placa, marca, modelo, ano, fotoUrl), ex.getMessage());
        }
    }

    @Get("/{id}/editar")
    public Object editar(
            @PathVariable Long id,
            @Nullable Session session,
            @Nullable @QueryValue String erro
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        Optional<Automovel> opt = automovelService.buscarPorId(id);
        if (opt.isEmpty()) {
            URI uri = UriBuilder.of("/agente/automoveis")
                    .queryParam("erro", "Automóvel não encontrado.")
                    .build();
            return HttpResponse.redirect(uri);
        }
        return formularioEdicao(agente.get(), opt.get(), erro);
    }

    @Post(value = "/{id}/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object salvar(
            @PathVariable Long id,
            @Nullable Session session,
            String placa,
            String marca,
            String modelo,
            Integer ano,
            @Nullable String fotoUrl
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        try {
            automovelService.atualizar(id, placa, marca, modelo, ano, fotoUrl);
            URI uri = UriBuilder.of("/agente/automoveis")
                    .queryParam("mensagem", "Automóvel atualizado com sucesso.")
                    .build();
            return HttpResponse.seeOther(uri);
        } catch (IllegalStateException ex) {
            Optional<Automovel> opt = automovelService.buscarPorId(id);
            if (opt.isEmpty()) {
                URI uri = UriBuilder.of("/agente/automoveis")
                        .queryParam("erro", "Automóvel não encontrado.")
                        .build();
                return HttpResponse.seeOther(uri);
            }
            Automovel a = opt.get();
            a.setPlacaNormalizada(placa != null ? placa : "");
            a.setMarca(marca != null ? marca : "");
            a.setModelo(modelo != null ? modelo : "");
            a.setAno(ano != null ? ano : a.getAno());
            a.setFotoUrl(fotoUrl != null ? fotoUrl : "");
            return formularioEdicao(agente.get(), a, ex.getMessage());
        }
    }

    private ModelAndView<Map<String, Object>> formulario(AgenteSessao agente, @Nullable Automovel dados, @Nullable String erro) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.nomeExibicao());
        model.put("erro", erro);
        model.put("modoEdicao", false);
        model.put("formAction", "/agente/automoveis");
        if (dados != null) {
            model.put("placa", dados.getPlacaNormalizada());
            model.put("marca", dados.getMarca());
            model.put("modelo", dados.getModelo());
            model.put("ano", dados.getAno());
            model.put("fotoUrl", dados.getFotoUrl());
        } else {
            model.put("placa", "");
            model.put("marca", "");
            model.put("modelo", "");
            model.put("ano", null);
            model.put("fotoUrl", "");
        }
        return new ModelAndView<>("agente/automoveis/formulario", model);
    }

    private ModelAndView<Map<String, Object>> formularioEdicao(AgenteSessao agente, Automovel a, @Nullable String erro) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.nomeExibicao());
        model.put("erro", erro);
        model.put("modoEdicao", true);
        model.put("automovelId", a.getId());
        model.put("formAction", "/agente/automoveis/" + a.getId() + "/editar");
        model.put("placa", a.getPlacaNormalizada());
        model.put("marca", a.getMarca());
        model.put("modelo", a.getModelo());
        model.put("ano", a.getAno());
        model.put("fotoUrl", a.getFotoUrl());
        return new ModelAndView<>("agente/automoveis/formulario", model);
    }

    private static Automovel preencherForm(String placa, String marca, String modelo, Integer ano, @Nullable String fotoUrl) {
        Automovel a = new Automovel();
        a.setPlacaNormalizada(placa != null ? placa : "");
        a.setMarca(marca != null ? marca : "");
        a.setModelo(modelo != null ? modelo : "");
        a.setAno(ano != null ? ano : 0);
        a.setFotoUrl(fotoUrl != null ? fotoUrl : "");
        return a;
    }
}
