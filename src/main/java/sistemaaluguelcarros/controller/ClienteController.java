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
import sistemaaluguelcarros.service.ClienteService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/clientes")
public class ClienteController {

    private static final URI LOGIN_URI = URI.create("/login");
    private static final String MSG_ACESSO_NEGADO = "Você só pode acessar e alterar o seu próprio cadastro.";

    private final ClienteService clienteService;
    private final SessionAuthService sessionAuthService;

    public ClienteController(ClienteService clienteService, SessionAuthService sessionAuthService) {
        this.clienteService = clienteService;
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

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clientes", List.of(clienteAutenticado.get()));
        model.put("clienteAtual", clienteAutenticado.get());
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        model.put("clienteNome", clienteAutenticado.get().getNome());
        return new ModelAndView<>("clientes/lista", model);
    }

    @Get("/novo")
    public ModelAndView<Map<String, Object>> novo(@Nullable @QueryValue String erro) {
        return formularioModel(
                new Cliente(),
                "Cadastrar cliente",
                "/clientes",
                erro,
                true,
                "/login"
        );
    }

    @Get("/{id}/editar")
    public Object editar(@PathVariable Long id, @Nullable Session session) {
        if (!sessionAuthService.isAutenticado(session)) {
            return redirectLogin();
        }
        if (!sessionAuthService.isClienteDaSessao(session, id)) {
            return redirectComErro(MSG_ACESSO_NEGADO);
        }
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        if (cliente.isEmpty()) {
            return redirectComErro("Cliente não encontrado.");
        }
        return formularioModel(
                cliente.get(),
                "Editar cliente",
                "/clientes/" + id + "/editar",
                null,
                false,
                "/clientes"
        );
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object salvar(
            String nome,
            String cpf,
            @Nullable String rg,
            String endereco,
            @Nullable String profissao,
            @Nullable String senha,
            @Nullable String confirmacaoSenha
    ) {
        Cliente cliente = new Cliente(
                sanitize(nome),
                sanitize(cpf),
                nullable(rg),
                sanitize(endereco),
                nullable(profissao)
        );

        String erroValidacao = validarCamposObrigatorios(cliente);
        if (erroValidacao != null) {
            return formularioModel(cliente, "Cadastrar cliente", "/clientes", erroValidacao, true, "/login");
        }

        try {
            clienteService.cadastrarComSenha(cliente, senha, confirmacaoSenha);
            return redirectParaLoginComMensagem("Cadastro realizado. Faça login com seu CPF e senha.");
        } catch (IllegalStateException ex) {
            return formularioModel(cliente, "Cadastrar cliente", "/clientes", ex.getMessage(), true, "/login");
        }
    }

    @Post(value = "/{id}/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object atualizar(
            @PathVariable Long id,
            @Nullable Session session,
            String nome,
            String cpf,
            @Nullable String rg,
            String endereco,
            @Nullable String profissao
    ) {
        if (!sessionAuthService.isAutenticado(session)) {
            return redirectLogin();
        }
        if (!sessionAuthService.isClienteDaSessao(session, id)) {
            return redirectComErro(MSG_ACESSO_NEGADO);
        }

        Optional<Cliente> clienteExistente = clienteService.buscarPorId(id);
        if (clienteExistente.isEmpty()) {
            return redirectComErro("Cliente não encontrado.");
        }

        Cliente cliente = clienteExistente.get();
        cliente.setNome(sanitize(nome));
        cliente.setCpf(sanitize(cpf));
        cliente.setRg(nullable(rg));
        cliente.setEndereco(sanitize(endereco));
        cliente.setProfissao(nullable(profissao));

        String erroValidacao = validarCamposObrigatorios(cliente);
        if (erroValidacao != null) {
            return formularioModel(cliente, "Editar cliente", "/clientes/" + id + "/editar", erroValidacao, false, "/clientes");
        }

        try {
            clienteService.salvar(cliente);
            return redirectComMensagem("Cliente atualizado com sucesso.");
        } catch (IllegalStateException ex) {
            return formularioModel(cliente, "Editar cliente", "/clientes/" + id + "/editar", ex.getMessage(), false, "/clientes");
        }
    }

    @Post(value = "/{id}/excluir", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object excluir(@PathVariable Long id, @Nullable Session session) {
        if (!sessionAuthService.isAutenticado(session)) {
            return redirectLogin();
        }
        if (!sessionAuthService.isClienteDaSessao(session, id)) {
            return redirectComErro(MSG_ACESSO_NEGADO);
        }
        try {
            clienteService.excluir(id);
            sessionAuthService.limparSessao(session);
            return redirectParaLoginComMensagem("Cliente excluído com sucesso.");
        } catch (IllegalStateException ex) {
            return redirectComErro("Não foi possível excluir o cliente. " + ex.getMessage());
        }
    }

    private ModelAndView<Map<String, Object>> formularioModel(
            Cliente cliente,
            String titulo,
            String action,
            @Nullable String erro,
            boolean cadastroComSenha,
            String cancelUrl
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("cliente", cliente);
        model.put("titulo", titulo);
        model.put("formAction", action);
        model.put("erro", erro);
        model.put("cadastroComSenha", cadastroComSenha);
        model.put("cancelUrl", cancelUrl);
        return new ModelAndView<>("clientes/formulario", model);
    }

    private MutableHttpResponse<?> redirectComMensagem(String mensagem) {
        URI uri = UriBuilder.of("/clientes")
                .queryParam("mensagem", mensagem)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectComErro(String erro) {
        URI uri = UriBuilder.of("/clientes")
                .queryParam("erro", erro)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectParaLoginComMensagem(String mensagem) {
        URI uri = UriBuilder.of("/login")
                .queryParam("mensagem", mensagem)
                .build();
        return HttpResponse.redirect(uri);
    }

    private MutableHttpResponse<?> redirectLogin() {
        return HttpResponse.redirect(LOGIN_URI);
    }

    @Nullable
    private String validarCamposObrigatorios(Cliente cliente) {
        if (isBlank(cliente.getNome())) {
            return "Nome é obrigatório.";
        }
        if (isBlank(cliente.getCpf())) {
            return "CPF é obrigatório.";
        }
        if (isBlank(cliente.getEndereco())) {
            return "Endereço é obrigatório.";
        }
        return null;
    }

    private boolean isBlank(@Nullable String valor) {
        return valor == null || valor.isBlank();
    }

    private String sanitize(String valor) {
        return valor == null ? "" : valor.trim();
    }

    @Nullable
    private String nullable(@Nullable String valor) {
        if (valor == null) {
            return null;
        }
        String trimmed = valor.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
