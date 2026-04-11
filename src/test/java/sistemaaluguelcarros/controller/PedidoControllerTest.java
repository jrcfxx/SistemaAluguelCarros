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
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.service.ContratoService;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
    private ContratoService contratoService;

    @Mock
    private Session session;

    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
        pedidoController = new PedidoController(pedidoAluguelService, sessionAuthService, contratoService);
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
        assertThat(mv.getView().orElseThrow()).isEqualTo("pedidos/lista");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("clienteNome")).isEqualTo("Julia Fiorini");
        assertThat(model.get("clienteId")).isEqualTo(1L);
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
        assertThat(mv.getView().orElseThrow()).isEqualTo("pedidos/formulario");
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

    @Test
    @DisplayName("deve redirecionar para login ao abrir edição sem autenticação")
    void deveRedirecionarParaLoginAoEditarSemAutenticacao() {
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = pedidoController.editar(1L, session, null, null);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
    }

    @Test
    @DisplayName("deve redirecionar para lista quando pedido não pertence ao cliente")
    void deveRedirecionarListaQuandoPedidoNaoPertenceAoCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.buscarPorIdECliente(99L, 1L)).thenReturn(Optional.empty());

        Object resposta = pedidoController.editar(99L, session, null, null);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        MutableHttpResponse<?> r = (MutableHttpResponse<?>) resposta;
        assertThat(r.getHeaders().getFirst("Location")).hasValueSatisfying(loc -> assertThat(loc).contains("/pedidos"));
    }

    @Test
    @DisplayName("deve exibir formulário de edição para pedido pendente do cliente")
    void deveExibirFormularioEdicaoParaPedidoPendente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Uso urbano");
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.buscarPorIdECliente(5L, 1L)).thenReturn(Optional.of(pedido));

        Object resposta = pedidoController.editar(5L, session, null, null);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView().orElseThrow()).isEqualTo("pedidos/formulario");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("modoEdicao")).isEqualTo(true);
        assertThat(model.get("formAction")).isEqualTo("/pedidos/5/editar");
    }

    @Test
    @DisplayName("deve salvar edição quando pedido pertence ao cliente autenticado")
    void deveSalvarEdicaoQuandoPedidoDoCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Antigo");
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.buscarPorIdECliente(5L, 1L)).thenReturn(Optional.of(pedido));
        when(pedidoAluguelService.atualizarPedido(1L, 5L, "Novo texto")).thenReturn(pedido);

        Object resposta = pedidoController.salvarEdicao(5L, session, "Novo texto");

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService).atualizarPedido(1L, 5L, "Novo texto");
    }

    @Test
    @DisplayName("não deve atualizar pedido de outro cliente na edição")
    void naoDeveAtualizarPedidoDeOutroCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.buscarPorIdECliente(5L, 1L)).thenReturn(Optional.empty());

        Object resposta = pedidoController.salvarEdicao(5L, session, "Texto");

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService, never()).atualizarPedido(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("deve cancelar pedido do cliente autenticado")
    void deveCancelarPedidoDoCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "X");
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.CANCELADO);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.cancelarPedido(1L, 5L)).thenReturn(pedido);

        Object resposta = pedidoController.cancelar(5L, session);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService).cancelarPedido(1L, 5L);
    }

    @Test
    @DisplayName("deve redirecionar para login ao cancelar sem autenticação")
    void deveRedirecionarLoginAoCancelarSemAutenticacao() {
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = pedidoController.cancelar(1L, session);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
    }

    @Test
    @DisplayName("não deve cancelar pedido de outro cliente")
    void naoDeveCancelarPedidoDeOutroCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(pedidoAluguelService.cancelarPedido(1L, 5L)).thenThrow(new IllegalStateException("Pedido não encontrado."));

        Object resposta = pedidoController.cancelar(5L, session);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService).cancelarPedido(1L, 5L);
    }

    @Test
    @DisplayName("deve exibir contrato quando cliente é dono do pedido aprovado")
    void deveExibirContratoQuandoClienteDono() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Descrição longa o suficiente para o pedido de teste.");
        pedido.setId(3L);
        pedido.setStatus(StatusPedido.APROVADO);
        Contrato contrato = new Contrato();
        contrato.setId(40L);
        contrato.setPedido(pedido);

        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(contratoService.buscarPorPedidoDoCliente(3L, 1L)).thenReturn(Optional.of(contrato));

        Object resposta = pedidoController.visualizarContrato(3L, session);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView().orElseThrow()).isEqualTo("contrato/visualizar");
    }

    @Test
    @DisplayName("deve redirecionar quando contrato não pertence ao cliente")
    void deveRedirecionarQuandoContratoNaoDoCliente() {
        Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
        when(sessionAuthService.clienteAutenticado(session)).thenReturn(Optional.of(cliente));
        when(contratoService.buscarPorPedidoDoCliente(3L, 1L)).thenReturn(Optional.empty());

        Object resposta = pedidoController.visualizarContrato(3L, session);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
    }

    private static Cliente novoCliente(Long id, String nome, String cpf) {
        Cliente cliente = new Cliente(nome, cpf, null, "Rua A", "Dev");
        cliente.setId(id);
        return cliente;
    }
}
