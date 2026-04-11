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
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.service.AgenteAuthService;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.ContratoService;
import sistemaaluguelcarros.service.PedidoAluguelService;
import sistemaaluguelcarros.service.RendimentoService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgenteController")
class AgenteControllerTest {

    @Mock
    private AgenteAuthService agenteAuthService;

    @Mock
    private AgenteSessionService agenteSessionService;

    @Mock
    private SessionAuthService sessionAuthService;

    @Mock
    private PedidoAluguelService pedidoAluguelService;

    @Mock
    private ContratoService contratoService;

    @Mock
    private RendimentoService rendimentoService;

    @Mock
    private Session session;

    private AgenteController agenteController;

    @BeforeEach
    void setUp() {
        agenteController = new AgenteController(
                agenteAuthService,
                agenteSessionService,
                sessionAuthService,
                pedidoAluguelService,
                contratoService,
                rendimentoService
        );
    }

    @Test
    @DisplayName("deve redirecionar para login quando lista sem sessão do agente")
    void deveRedirecionarParaLoginQuandoListaSemSessaoDoAgente() {
        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = agenteController.listarPedidos(session, null, null);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
    }

    @Test
    @DisplayName("deve listar pedidos para agente autenticado")
    void deveListarPedidosParaAgenteAutenticado() {
        AgenteSessao agente = new AgenteSessao("analista", "Agente Financeiro");
        PedidoAluguel pedido = novoPedido(10L, 1L, "Uso urbano", StatusPedido.PENDENTE);

        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.of(agente));
        when(pedidoAluguelService.listarParaAnalise()).thenReturn(List.of(pedido));
        when(pedidoAluguelService.contarPorStatus(StatusPedido.PENDENTE)).thenReturn(1L);
        when(pedidoAluguelService.contarPorStatus(StatusPedido.APROVADO)).thenReturn(0L);
        when(pedidoAluguelService.contarPorStatus(StatusPedido.REPROVADO)).thenReturn(0L);
        when(pedidoAluguelService.contarPorStatus(StatusPedido.CANCELADO)).thenReturn(0L);

        Object resposta = agenteController.listarPedidos(session, null, null);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView().orElseThrow()).isEqualTo("agente/pedidos/lista");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("agenteNome")).isEqualTo("Agente Financeiro");
        assertThat((List<?>) model.get("pedidos")).hasSize(1);
    }

    @Test
    @DisplayName("deve autenticar agente e redirecionar para painel")
    void deveAutenticarAgenteERedirecionarParaPainel() {
        AgenteSessao agente = new AgenteSessao("analista", "Agente Financeiro");
        when(agenteAuthService.autenticar("analista", "segredo")).thenReturn(Optional.of(agente));

        Object resposta = agenteController.login("analista", "segredo", session);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(sessionAuthService).limparSessao(session);
        verify(agenteSessionService).autenticar(session, agente);
    }

    @Test
    @DisplayName("deve abrir detalhe do pedido para agente autenticado")
    void deveAbrirDetalheDoPedidoParaAgenteAutenticado() {
        AgenteSessao agente = new AgenteSessao("analista", "Agente Financeiro");
        PedidoAluguel pedido = novoPedido(12L, 5L, "Viagem de 7 dias", StatusPedido.PENDENTE);

        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.of(agente));
        when(pedidoAluguelService.buscarDetalheParaAnalise(12L)).thenReturn(Optional.of(pedido));
        when(rendimentoService.listarPorCliente(5L)).thenReturn(List.of());

        Object resposta = agenteController.detalharPedido(12L, session, null, null);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView().orElseThrow()).isEqualTo("agente/pedidos/detalhe");
        Map<?, ?> model = (Map<?, ?>) mv.getModel().get();
        assertThat(model.get("pedido")).isEqualTo(pedido);
    }

    @Test
    @DisplayName("deve aprovar pedido quando agente está autenticado")
    void deveAprovarPedidoQuandoAgenteAutenticado() {
        AgenteSessao agente = new AgenteSessao("analista", "Agente Financeiro");
        PedidoAluguel pedido = novoPedido(5L, 1L, "Descrição longa o suficiente para o pedido.", StatusPedido.APROVADO);

        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.of(agente));
        when(pedidoAluguelService.aprovarPedido(5L)).thenReturn(pedido);

        Object resposta = agenteController.aprovarPedido(5L, session);

        assertThat(resposta).isInstanceOf(MutableHttpResponse.class);
        verify(pedidoAluguelService).aprovarPedido(5L);
    }

    @Test
    @DisplayName("deve redirecionar ao aprovar sem sessão do agente")
    void deveRedirecionarAoAprovarSemAgente() {
        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.empty());

        Object resposta = agenteController.aprovarPedido(1L, session);

        assertThat(resposta).isInstanceOf(HttpResponse.class);
        verify(pedidoAluguelService, never()).aprovarPedido(anyLong());
    }

    @Test
    @DisplayName("deve exibir contrato para agente autenticado")
    void deveExibirContratoParaAgenteAutenticado() {
        AgenteSessao agente = new AgenteSessao("analista", "Agente Financeiro");
        PedidoAluguel pedido = novoPedido(3L, 2L, "Descrição longa o suficiente para o pedido.", StatusPedido.APROVADO);
        Contrato contrato = new Contrato();
        contrato.setId(77L);
        contrato.setPedido(pedido);

        when(agenteSessionService.agenteAutenticado(session)).thenReturn(Optional.of(agente));
        when(contratoService.buscarParaExibicaoAgente(77L)).thenReturn(Optional.of(contrato));

        Object resposta = agenteController.visualizarContrato(77L, session);

        assertThat(resposta).isInstanceOf(ModelAndView.class);
        ModelAndView<?> mv = (ModelAndView<?>) resposta;
        assertThat(mv.getView().orElseThrow()).isEqualTo("contrato/visualizar");
    }

    private static PedidoAluguel novoPedido(Long id, Long clienteId, String descricao, StatusPedido status) {
        Cliente cliente = new Cliente("Julia", "12345678900", "112233", "Rua A", "Dev");
        cliente.setId(clienteId);
        PedidoAluguel pedido = new PedidoAluguel(cliente, descricao);
        pedido.setId(id);
        pedido.setStatus(status);
        return pedido;
    }
}
