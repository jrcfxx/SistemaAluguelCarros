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
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.net.URI;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/pedidos")
public class PedidoController {

    private static final URI LOGIN_URI = URI.create("/login");
    private static final String MSG_PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado ou indisponível.";

    private final PedidoAluguelService pedidoAluguelService;
    private final SessionAuthService sessionAuthService;

    public PedidoController(
            PedidoAluguelService pedidoAluguelService,
            SessionAuthService sessionAuthService
    ) {
        this.pedidoAluguelService = pedidoAluguelService;
        this.sessionAuthService = sessionAuthService;
    }

    @Get
    public Object listar(
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        List<PedidoAluguel> pedidos = pedidoAluguelService.listarPorCliente(clienteAutenticado.get().getId());
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clienteNome", clienteAutenticado.get().getNome());
        model.put("clienteCpf", clienteAutenticado.get().getCpf());
        model.put("pedidos", pedidos);
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        return new ModelAndView<>("pedidos/lista", model);
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

        return formularioCriacao(clienteAutenticado.get(), "", mensagem, erro);
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
            return redirectListaComMensagem(
                    "Pedido criado com sucesso. Status inicial: " + pedido.getStatus() + "."
            );
        } catch (IllegalStateException ex) {
            return formularioCriacao(clienteAutenticado.get(), descricaoSolicitacao, null, ex.getMessage());
        }
    }

    @Get("/{id}/editar")
    public Object editar(
            @PathVariable Long id,
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        Optional<PedidoAluguel> pedidoOpt = pedidoAluguelService.buscarPorIdECliente(id, clienteAutenticado.get().getId());
        if (pedidoOpt.isEmpty()) {
            return redirectListaComErro(MSG_PEDIDO_NAO_ENCONTRADO);
        }

        PedidoAluguel pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            return redirectListaComErro(
                    "Este pedido não pode ser editado porque o status atual é " + pedido.getStatus()
                            + ". Apenas pedidos PENDENTES podem ser alterados."
            );
        }

        return formularioEdicao(
                clienteAutenticado.get(),
                pedido,
                mensagem,
                erro
        );
    }

    @Post(value = "/{id}/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object salvarEdicao(
            @PathVariable Long id,
            @Nullable Session session,
            String descricaoSolicitacao
    ) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        Optional<PedidoAluguel> pedidoOpt = pedidoAluguelService.buscarPorIdECliente(id, clienteAutenticado.get().getId());
        if (pedidoOpt.isEmpty()) {
            return redirectListaComErro(MSG_PEDIDO_NAO_ENCONTRADO);
        }

        try {
            pedidoAluguelService.atualizarPedido(clienteAutenticado.get().getId(), id, descricaoSolicitacao);
            return redirectListaComMensagem("Pedido atualizado com sucesso.");
        } catch (IllegalStateException ex) {
            if ("Pedido não encontrado.".equals(ex.getMessage())) {
                return redirectListaComErro(MSG_PEDIDO_NAO_ENCONTRADO);
            }
            PedidoAluguel pedidoForm = pedidoOpt.get();
            pedidoForm.setDescricaoSolicitacao(descricaoSolicitacao != null ? descricaoSolicitacao : "");
            return formularioEdicao(clienteAutenticado.get(), pedidoForm, null, ex.getMessage());
        }
    }

    @Post(value = "/{id}/cancelar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object cancelar(@PathVariable Long id, @Nullable Session session) {
        Optional<Cliente> clienteAutenticado = sessionAuthService.clienteAutenticado(session);
        if (clienteAutenticado.isEmpty()) {
            return redirectLogin();
        }

        try {
            pedidoAluguelService.cancelarPedido(clienteAutenticado.get().getId(), id);
            return redirectListaComMensagem("Pedido cancelado com sucesso.");
        } catch (IllegalStateException ex) {
            if ("Pedido não encontrado.".equals(ex.getMessage())) {
                return redirectListaComErro(MSG_PEDIDO_NAO_ENCONTRADO);
            }
            return redirectListaComErro(ex.getMessage());
        }
    }

    private ModelAndView<Map<String, Object>> formularioCriacao(
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
        model.put("modoEdicao", false);
        model.put("titulo", "Novo pedido");
        model.put("formAction", "/pedidos");
        model.put("voltarUrl", "/clientes");
        return new ModelAndView<>("pedidos/formulario", model);
    }

    private ModelAndView<Map<String, Object>> formularioEdicao(
            Cliente cliente,
            PedidoAluguel pedido,
            @Nullable String mensagem,
            @Nullable String erro
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clienteNome", cliente.getNome());
        model.put("clienteCpf", cliente.getCpf());
        model.put("descricaoSolicitacao", pedido.getDescricaoSolicitacao());
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        model.put("modoEdicao", true);
        model.put("titulo", "Editar pedido");
        model.put("formAction", "/pedidos/" + pedido.getId() + "/editar");
        model.put("voltarUrl", "/pedidos");
        model.put("pedidoId", pedido.getId());
        model.put("pedidoStatus", pedido.getStatus());
        return new ModelAndView<>("pedidos/formulario", model);
    }

    private MutableHttpResponse<?> redirectListaComMensagem(String mensagem) {
        URI uri = UriBuilder.of("/pedidos")
                .queryParam("mensagem", mensagem)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectListaComErro(String erro) {
        URI uri = UriBuilder.of("/pedidos")
                .queryParam("erro", erro)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectLogin() {
        return HttpResponse.redirect(LOGIN_URI);
    }
}
