package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.service.AgenteAuthService;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.ContratoService;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.RendimentoService;
import sistemaaluguelcarros.service.SessionAuthService;
import sistemaaluguelcarros.service.TipoContratoResolver;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/agente")
public class AgenteController {

    private static final String MSG_LOGIN_INVALIDO = "Usuário ou senha do agente inválidos.";

    private final AgenteAuthService agenteAuthService;
    private final AgenteSessionService agenteSessionService;
    private final SessionAuthService sessionAuthService;
    private final PedidoAluguelService pedidoAluguelService;
    private final ContratoService contratoService;
    private final RendimentoService rendimentoService;

    public AgenteController(
            AgenteAuthService agenteAuthService,
            AgenteSessionService agenteSessionService,
            SessionAuthService sessionAuthService,
            PedidoAluguelService pedidoAluguelService,
            ContratoService contratoService,
            RendimentoService rendimentoService
    ) {
        this.agenteAuthService = agenteAuthService;
        this.agenteSessionService = agenteSessionService;
        this.sessionAuthService = sessionAuthService;
        this.pedidoAluguelService = pedidoAluguelService;
        this.contratoService = contratoService;
        this.rendimentoService = rendimentoService;
    }

    @Get("/login")
    public Object loginForm(
            @Nullable Session session,
            @Nullable @QueryValue String erro,
            @Nullable @QueryValue String mensagem
    ) {
        if (agenteSessionService.isAutenticado(session)) {
            return HttpResponse.redirect(URI.create("/agente/pedidos"));
        }

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("erro", erro);
        model.put("mensagem", mensagem);
        return new ModelAndView<>("agente/login", model);
    }

    @Post(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object login(String username, String senha, Session session) {
        Optional<AgenteSessao> agente = agenteAuthService.autenticar(username, senha);
        if (agente.isEmpty()) {
            URI uri = UriBuilder.of("/agente/login")
                    .queryParam("erro", MSG_LOGIN_INVALIDO)
                    .build();
            return HttpResponse.seeOther(uri);
        }

        sessionAuthService.limparSessao(session);
        agenteSessionService.autenticar(session, agente.get());
        return HttpResponse.seeOther(URI.create("/agente/pedidos"));
    }

    @Post(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> logout(@Nullable Session session) {
        agenteSessionService.limparSessao(session);
        return HttpResponse.seeOther(URI.create("/agente/login"));
    }

    @Get("/pedidos")
    public Object listarPedidos(
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(URI.create("/agente/login"));
        }

        List<PedidoAluguel> pedidos = pedidoAluguelService.listarParaAnalise();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.get().nomeExibicao());
        model.put("pedidos", pedidos);
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        model.put("totalPedidos", pedidos.size());
        model.put("pendentes", pedidoAluguelService.contarPorStatus(StatusPedido.PENDENTE));
        model.put("aprovados", pedidoAluguelService.contarPorStatus(StatusPedido.APROVADO));
        model.put("reprovados", pedidoAluguelService.contarPorStatus(StatusPedido.REPROVADO));
        model.put("cancelados", pedidoAluguelService.contarPorStatus(StatusPedido.CANCELADO));
        return new ModelAndView<>("agente/pedidos/lista", model);
    }

    @Get("/pedidos/{id}")
    public Object detalharPedido(
            @PathVariable Long id,
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(URI.create("/agente/login"));
        }

        Optional<PedidoAluguel> pedidoOpt = pedidoAluguelService.buscarDetalheParaAnalise(id);
        if (pedidoOpt.isEmpty()) {
            URI uri = UriBuilder.of("/agente/pedidos")
                    .queryParam("erro", "Pedido não encontrado.")
                    .build();
            return HttpResponse.redirect(uri);
        }

        PedidoAluguel pedido = pedidoOpt.get();
        Long clienteId = pedido.getCliente().getId();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.get().nomeExibicao());
        model.put("pedido", pedido);
        model.put("cliente", pedido.getCliente());
        model.put("rendimentos", rendimentoService.listarPorCliente(clienteId));
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        return new ModelAndView<>("agente/pedidos/detalhe", model);
    }

    @Post(value = "/pedidos/{id}/aprovar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object aprovarPedido(
            @PathVariable Long id,
            @Nullable Session session,
            @Nullable String tipoContrato
    ) {
        if (agenteSessionService.agenteAutenticado(session).isEmpty()) {
            return HttpResponse.redirect(URI.create("/agente/login"));
        }
        try {
            pedidoAluguelService.aprovarPedido(id, TipoContratoResolver.resolver(tipoContrato));
            URI uri = UriBuilder.of("/agente/pedidos/" + id)
                    .queryParam("mensagem", "Pedido aprovado e contrato gerado com sucesso.")
                    .build();
            return HttpResponse.seeOther(uri);
        } catch (IllegalStateException ex) {
            URI uri = UriBuilder.of("/agente/pedidos/" + id)
                    .queryParam("erro", ex.getMessage())
                    .build();
            return HttpResponse.seeOther(uri);
        }
    }

    @Post(value = "/pedidos/{id}/reprovar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object reprovarPedido(@PathVariable Long id, @Nullable Session session) {
        if (agenteSessionService.agenteAutenticado(session).isEmpty()) {
            return HttpResponse.redirect(URI.create("/agente/login"));
        }
        try {
            pedidoAluguelService.reprovarPedido(id);
            URI uri = UriBuilder.of("/agente/pedidos/" + id)
                    .queryParam("mensagem", "Pedido reprovado.")
                    .build();
            return HttpResponse.seeOther(uri);
        } catch (IllegalStateException ex) {
            URI uri = UriBuilder.of("/agente/pedidos/" + id)
                    .queryParam("erro", ex.getMessage())
                    .build();
            return HttpResponse.seeOther(uri);
        }
    }

    @Get("/contratos/{id}")
    public Object visualizarContrato(@PathVariable Long id, @Nullable Session session) {
        Optional<AgenteSessao> agente = agenteSessionService.agenteAutenticado(session);
        if (agente.isEmpty()) {
            return HttpResponse.redirect(URI.create("/agente/login"));
        }

        Optional<Contrato> contratoOpt = contratoService.buscarParaExibicaoAgente(id);
        if (contratoOpt.isEmpty()) {
            URI uri = UriBuilder.of("/agente/pedidos")
                    .queryParam("erro", "Contrato não encontrado.")
                    .build();
            return HttpResponse.redirect(uri);
        }

        Contrato contrato = contratoOpt.get();
        PedidoAluguel pedido = contrato.getPedido();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agenteNome", agente.get().nomeExibicao());
        model.put("contrato", contrato);
        model.put("pedido", pedido);
        model.put("cliente", pedido.getCliente());
        model.put("tituloPagina", "Contrato (agente)");
        model.put("voltarUrl", "/agente/pedidos/" + pedido.getId());
        return new ModelAndView<>("contrato/visualizar", model);
    }
}
