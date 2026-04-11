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
import sistemaaluguelcarros.domain.Rendimento;
import sistemaaluguelcarros.service.RendimentoService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/clientes")
public class RendimentoController {

    private static final URI LOGIN_URI = URI.create("/login");
    private static final String MSG_ACESSO_NEGADO = "Você só pode gerenciar os rendimentos da sua própria conta.";

    private final RendimentoService rendimentoService;
    private final SessionAuthService sessionAuthService;

    public RendimentoController(RendimentoService rendimentoService, SessionAuthService sessionAuthService) {
        this.rendimentoService = rendimentoService;
        this.sessionAuthService = sessionAuthService;
    }

    @Get("/{clienteId}/rendimentos")
    public Object listar(
            @PathVariable Long clienteId,
            @Nullable Session session,
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        if (!sessionAuthService.isAutenticado(session)) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        if (!sessionAuthService.isClienteDaSessao(session, clienteId)) {
            return redirectComErro(MSG_ACESSO_NEGADO);
        }

        Optional<Cliente> clienteOpt = sessionAuthService.clienteAutenticado(session);
        List<Rendimento> rendimentos = rendimentoService.listarPorCliente(clienteId);

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clienteId", clienteId);
        model.put("clienteNome", clienteOpt.map(Cliente::getNome).orElse(""));
        model.put("rendimentos", rendimentos);
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        model.put("podeAdicionar", rendimentoService.clientePodeAdicionar(clienteId));
        model.put("limiteRendimentos", 3);
        model.put("totalRendimentos", rendimentos.size());
        return new ModelAndView<>("clientes/rendimentos/lista", model);
    }

    @Post(value = "/{clienteId}/rendimentos", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object adicionar(
            @PathVariable Long clienteId,
            @Nullable Session session,
            String nomeEmpregador,
            @Nullable String cnpj,
            String valorMensal
    ) {
        if (!sessionAuthService.isAutenticado(session)) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        if (!sessionAuthService.isClienteDaSessao(session, clienteId)) {
            return redirectComErroPost(clienteId, MSG_ACESSO_NEGADO);
        }

        BigDecimal valor;
        try {
            valor = parseValorMonetario(valorMensal);
        } catch (IllegalArgumentException ex) {
            return redirectComErroPost(clienteId, ex.getMessage());
        }

        try {
            rendimentoService.adicionar(clienteId, nomeEmpregador, cnpj, valor);
            return redirectComMensagemPost(clienteId, "Rendimento cadastrado com sucesso.");
        } catch (IllegalStateException ex) {
            return redirectComErroPost(clienteId, ex.getMessage());
        }
    }

    @Post(value = "/{clienteId}/rendimentos/{rendimentoId}/excluir", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object excluir(
            @PathVariable Long clienteId,
            @PathVariable Long rendimentoId,
            @Nullable Session session
    ) {
        if (!sessionAuthService.isAutenticado(session)) {
            return HttpResponse.redirect(LOGIN_URI);
        }
        if (!sessionAuthService.isClienteDaSessao(session, clienteId)) {
            return redirectComErroPost(clienteId, MSG_ACESSO_NEGADO);
        }

        try {
            rendimentoService.excluir(clienteId, rendimentoId);
            return redirectComMensagemPost(clienteId, "Rendimento removido.");
        } catch (IllegalStateException ex) {
            return redirectComErroPost(clienteId, ex.getMessage());
        }
    }

    private BigDecimal parseValorMonetario(String valorBruto) {
        if (valorBruto == null || valorBruto.isBlank()) {
            throw new IllegalArgumentException("Informe o valor mensal do rendimento.");
        }
        String normalizado = valorBruto.trim().replace(',', '.');
        try {
            return new BigDecimal(normalizado).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor inválido. Use números ou vírgula decimal (ex.: 3500,50).");
        }
    }

    private MutableHttpResponse<?> redirectComMensagemPost(Long clienteId, String mensagem) {
        URI uri = UriBuilder.of("/clientes/" + clienteId + "/rendimentos")
                .queryParam("mensagem", mensagem)
                .build();
        return HttpResponse.seeOther(uri);
    }

    private MutableHttpResponse<?> redirectComErroPost(Long clienteId, String erro) {
        URI uri = UriBuilder.of("/clientes/" + clienteId + "/rendimentos")
                .queryParam("erro", erro)
                .build();
        return HttpResponse.seeOther(uri);
    }

    private MutableHttpResponse<?> redirectComErro(String erro) {
        URI uri = UriBuilder.of("/clientes")
                .queryParam("erro", erro)
                .build();
        return HttpResponse.redirect(uri);
    }
}
