package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/pedidos")
public class PedidoController {

    private static final URI LOGIN_URI = URI.create("/login");

    private final PedidoAluguelService pedidoAluguelService;
    private final SessionAuthService sessionAuthService;

    public PedidoController(
            PedidoAluguelService pedidoAluguelService,
            SessionAuthService sessionAuthService
    ) {
        this.pedidoAluguelService = pedidoAluguelService;
        this.sessionAuthService = sessionAuthService;
    }

    @Get("/novo")
    public Object novo(
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        return formularioModel(clienteAutenticado.get(), "", mensagem, erro);
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object criar(
            @Nullable Session session,
            String descricaoSolicitacao
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        try {
            PedidoAluguel pedido = pedidoAluguelService.criarPedido(
                    clienteAutenticado.get().getId(),
                    descricaoSolicitacao
            );
            return redirectComMensagem(
                    "Pedido criado com sucesso. Status inicial: " + pedido.getStatus() + "."
            );
        } catch (IllegalStateException ex) {
            return formularioModel(clienteAutenticado.get(), descricaoSolicitacao, null, ex.getMessage());
        }
    }

    private ModelAndView<Map<String, Object>> formularioModel(
            Cliente cliente,
            String descricaoSolicitacao,
            @Nullable String mensagem,
            @Nullable String erro
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clienteNome", cliente.getNome());
        model.put("clienteCpf", cliente.getCpf());
        model.put("descricaoSolicitacao", descricaoSolicitacao);
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        return new ModelAndView<>("pedidos/formulario", model);
    }

    private MutableHttpResponse<?> redirectComMensagem(String mensagem) {
        URI uri = UriBuilder.of("/pedidos/novo")
                .queryParam("mensagem", mensagem)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectLogin() {
        return HttpResponse.redirect(LOGIN_URI);
    }
}
