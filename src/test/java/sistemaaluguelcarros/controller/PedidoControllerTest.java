package sistemaaluguelcarros.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoController")
class PedidoControllerTest {

    @Mock
    private PedidoAluguelService pedidoAluguelService;

    @Mock
    private SessionAuthService sessionAuthService;

    @Mock
    private Session session;

    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
        pedidoController = new PedidoController(pedidoAluguelService, sessionAuthService);
    }

    @Test
    @DisplayName("deve redirecionar para login quando tenta abrir formulário sem autenticação")
    void deveRedirecionarParaLoginQuandoNaoAutenticado() {
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = pedidoController.novo(session, null, null);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
    }

    @Test
    @DisplayName("deve redirecionar para login quando tenta listar pedidos sem autenticação")
    void deveRedirecionarParaLoginQuandoListaSemAutenticacao() {
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = pedidoController.listar(session, null, null);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
    }

    @Test
    @DisplayName("deve listar pedidos do cliente autenticado")
    void deveListarPedidosDoClienteAutenticado() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Uso urbano");
        pedido.setId(7L);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.listarPorCliente(1L)).thenReturn(List.of(pedido));

        Object resposta = pedidoController.listar(session, "ok", null);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView()).isEqualTo("pedidos/lista");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("clienteNome")).isEqualTo("Julia Fiorini");
        assertThat((List<?>) model.get("pedidos")).hasSize(1);
    }

    @Test
    @DisplayName("deve montar formulário com dados do cliente autenticado")
    void deveMontarFormularioComClienteAutenticado() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));

        Object resposta = pedidoController.novo(session, "Pedido criado", null);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView()).isEqualTo("pedidos/formulario");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("clienteNome")).isEqualTo("Julia Fiorini");
    }

    @Test
    @DisplayName("deve criar pedido quando cliente autenticado envia solicitação válida")
    void deveCriarPedidoQuandoSolicitacaoValida() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Uso urbano");
        pedido.setId(7L);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.criarPedido(1L, "Uso urbano")).thenReturn(pedido);

        Object resposta = pedidoController.criar(session, "Uso urbano");

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService).criarPedido(1L, "Uso urbano");
    }

    private static Cliente novoCliente(Long id, String nome, String cpf) {
        Cliente cliente = new Cliente(nome, cpf, null, "Rua A", "Dev");
        cliente.setId(id);
        return cliente;
    }
}
