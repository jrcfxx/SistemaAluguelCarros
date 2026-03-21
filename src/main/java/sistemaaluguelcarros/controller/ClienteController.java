package sistemaaluguelcarros.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import jakarta.annotation.Nullable;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.ClienteService;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Get
    public ModelAndView<Map<String, Object>> listar(
            @Nullable @QueryValue String mensagem,
            @Nullable @QueryValue String erro
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("clientes", toList(clienteService.listarTodos()));
        model.put("mensagem", mensagem);
        model.put("erro", erro);
        return new ModelAndView<>("clientes/lista", model);
    }

    @Get("/novo")
    public ModelAndView<Map<String, Object>> novo(@Nullable @QueryValue String erro) {
        return formularioModel(new Cliente(), "Cadastrar cliente", "/clientes", erro);
    }

    @Get("/{id}/editar")
    public Object editar(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        if (cliente.isEmpty()) {
            return redirectComErro("Cliente não encontrado.");
        }
        return formularioModel(cliente.get(), "Editar cliente", "/clientes/" + id + "/editar", null);
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object salvar(
            String nome,
            String cpf,
            @Nullable String rg,
            String endereco,
            @Nullable String profissao
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
            return formularioModel(cliente, "Cadastrar cliente", "/clientes", erroValidacao);
        }

        try {
            clienteService.salvar(cliente);
            return redirectComMensagem("Cliente cadastrado com sucesso.");
        } catch (IllegalStateException ex) {
            return formularioModel(cliente, "Cadastrar cliente", "/clientes", ex.getMessage());
        }
    }

    @Post(value = "/{id}/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object atualizar(
            @PathVariable Long id,
            String nome,
            String cpf,
            @Nullable String rg,
            String endereco,
            @Nullable String profissao
    ) {
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
            return formularioModel(cliente, "Editar cliente", "/clientes/" + id + "/editar", erroValidacao);
        }

        try {
            clienteService.salvar(cliente);
            return redirectComMensagem("Cliente atualizado com sucesso.");
        } catch (IllegalStateException ex) {
            return formularioModel(cliente, "Editar cliente", "/clientes/" + id + "/editar", ex.getMessage());
        }
    }

    @Post("/{id}/excluir")
    public MutableHttpResponse<?> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return redirectComMensagem("Cliente excluído com sucesso.");
    }

    private ModelAndView<Map<String, Object>> formularioModel(
            Cliente cliente,
            String titulo,
            String action,
            @Nullable String erro
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("cliente", cliente);
        model.put("titulo", titulo);
        model.put("formAction", action);
        model.put("erro", erro);
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

    private List<Cliente> toList(Iterable<Cliente> clientes) {
        List<Cliente> lista = new ArrayList<>();
        for (Cliente cliente : clientes) {
            lista.add(cliente);
        }
        return lista;
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
