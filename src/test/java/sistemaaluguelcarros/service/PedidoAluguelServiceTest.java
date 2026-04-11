package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.repository.PedidoAluguelRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoAluguelService")
class PedidoAluguelServiceTest {

    @Mock
    private PedidoAluguelRepository pedidoAluguelRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ContratoService contratoService;

    @Mock
    private AutomovelRepository automovelRepository;

    private PedidoAluguelService pedidoAluguelService;

    @BeforeEach
    void setUp() {
        pedidoAluguelService = new PedidoAluguelService(
                pedidoAluguelRepository,
                clienteService,
                contratoService,
                automovelRepository
        );
    }

    @Nested
    @DisplayName("criarPedido")
    class CriarPedido {

        @Test
        @DisplayName("deve criar pedido com status pendente")
        void deveCriarPedidoComStatusPendente() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            Automovel automovel = novoAutomovel(50L);
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
            when(automovelRepository.findById(50L)).thenReturn(Optional.of(automovel));
            when(pedidoAluguelRepository.save(any(PedidoAluguel.class))).thenAnswer(invocation -> {
                PedidoAluguel pedido = invocation.getArgument(0);
                pedido.setId(10L);
                return pedido;
            });

            PedidoAluguel pedido = pedidoAluguelService.criarPedido(1L, 50L, " Aluguel de veículo para uso urbano ");

            assertThat(pedido.getId()).isEqualTo(10L);
            assertThat(pedido.getCliente().getId()).isEqualTo(1L);
            assertThat(pedido.getAutomovel().getId()).isEqualTo(50L);
            assertThat(pedido.getDescricaoSolicitacao()).isEqualTo("Aluguel de veículo para uso urbano");
            assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PENDENTE);
            assertThat(pedido.getDataSolicitacao()).isNotNull();
            assertThat(pedido.getUltimaAtualizacao()).isNotNull();
            verify(pedidoAluguelRepository).save(any(PedidoAluguel.class));
        }

        @Test
        @DisplayName("deve rejeitar pedido quando cliente não existe")
        void deveRejeitarQuandoClienteNaoExiste() {
            when(clienteService.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(99L, 1L, "Pedido de aluguel com sete dias"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente não encontrado.");

            verify(pedidoAluguelRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve rejeitar pedido sem descrição")
        void deveRejeitarQuandoDescricaoVazia() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            Automovel automovel = novoAutomovel(50L);
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
            when(automovelRepository.findById(50L)).thenReturn(Optional.of(automovel));

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(1L, 50L, "   "))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Descrição da solicitação é obrigatória.");

            verify(pedidoAluguelRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve rejeitar pedido com descrição muito curta")
        void deveRejeitarQuandoDescricaoMuitoCurta() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            Automovel automovel = novoAutomovel(50L);
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
            when(automovelRepository.findById(50L)).thenReturn(Optional.of(automovel));

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(1L, 50L, "Curta demais"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("entre 15 e 1000 caracteres");

            verify(pedidoAluguelRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve rejeitar pedido sem automóvel selecionado")
        void deveRejeitarQuandoAutomovelNulo() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(1L, null, "Pedido de aluguel com sete dias"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Selecione o automóvel desejado no pedido.");

            verify(pedidoAluguelRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("deve listar pedidos por cliente")
        void deveListarPedidosPorCliente() {
            PedidoAluguel pedido1 = novoPedido(1L, 1L, "Pedido A", StatusPedido.PENDENTE);
            PedidoAluguel pedido2 = novoPedido(2L, 1L, "Pedido B", StatusPedido.APROVADO);
            when(pedidoAluguelRepository.findByClienteIdOrderByDataSolicitacaoDesc(1L))
                    .thenReturn(List.of(pedido2, pedido1));

            List<PedidoAluguel> pedidos = pedidoAluguelService.listarPorCliente(1L);

            assertThat(pedidos).hasSize(2);
            assertThat(pedidos.get(0).getId()).isEqualTo(2L);
            verify(pedidoAluguelRepository).findByClienteIdOrderByDataSolicitacaoDesc(1L);
        }

        @Test
        @DisplayName("deve buscar pedido por id e cliente")
        void deveBuscarPedidoPorIdECliente() {
            PedidoAluguel pedido = novoPedido(5L, 1L, "Pedido único", StatusPedido.PENDENTE);
            when(pedidoAluguelRepository.findByIdAndClienteId(5L, 1L)).thenReturn(Optional.of(pedido));

            Optional<PedidoAluguel> resultado = pedidoAluguelService.buscarPorIdECliente(5L, 1L);

            assertThat(resultado).contains(pedido);
            verify(pedidoAluguelRepository).findByIdAndClienteId(5L, 1L);
        }

        @Test
        @DisplayName("deve listar pedidos para análise do agente")
        void deveListarPedidosParaAnaliseDoAgente() {
            PedidoAluguel pedido1 = novoPedido(1L, 1L, "Pedido A", StatusPedido.PENDENTE);
            PedidoAluguel pedido2 = novoPedido(2L, 2L, "Pedido B", StatusPedido.CANCELADO);
            when(pedidoAluguelRepository.listarParaAnalise()).thenReturn(List.of(pedido2, pedido1));

            List<PedidoAluguel> pedidos = pedidoAluguelService.listarParaAnalise();

            assertThat(pedidos).hasSize(2);
            assertThat(pedidos.get(0).getId()).isEqualTo(2L);
            verify(pedidoAluguelRepository).listarParaAnalise();
        }

        @Test
        @DisplayName("deve contar pedidos por status")
        void deveContarPedidosPorStatus() {
            when(pedidoAluguelRepository.countByStatus(StatusPedido.PENDENTE)).thenReturn(3L);

            long total = pedidoAluguelService.contarPorStatus(StatusPedido.PENDENTE);

            assertThat(total).isEqualTo(3L);
            verify(pedidoAluguelRepository).countByStatus(StatusPedido.PENDENTE);
        }

        @Test
        @DisplayName("deve buscar detalhe do pedido para análise")
        void deveBuscarDetalheDoPedidoParaAnalise() {
            PedidoAluguel pedido = novoPedido(7L, 3L, "Pedido detalhado", StatusPedido.PENDENTE);
            when(pedidoAluguelRepository.buscarDetalhePorId(7L)).thenReturn(Optional.of(pedido));

            Optional<PedidoAluguel> resultado = pedidoAluguelService.buscarDetalheParaAnalise(7L);

            assertThat(resultado).contains(pedido);
            verify(pedidoAluguelRepository).buscarDetalhePorId(7L);
        }
    }

    @Nested
    @DisplayName("atualizarPedido")
    class AtualizarPedido {

        @Test
        @DisplayName("deve atualizar descrição quando pedido está pendente")
        void deveAtualizarQuandoPendente() {
            PedidoAluguel pedido = novoPedido(8L, 2L, "Descrição antiga detalhada", StatusPedido.PENDENTE);
            when(pedidoAluguelRepository.findByIdAndClienteId(8L, 2L)).thenReturn(Optional.of(pedido));
            when(pedidoAluguelRepository.update(any(PedidoAluguel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PedidoAluguel atualizado = pedidoAluguelService.atualizarPedido(2L, 8L, " Nova descrição detalhada ");

            assertThat(atualizado.getDescricaoSolicitacao()).isEqualTo("Nova descrição detalhada");
            verify(pedidoAluguelRepository).update(eq(pedido));
        }

        @Test
        @DisplayName("deve bloquear atualização quando pedido não está pendente")
        void deveBloquearQuandoNaoPendente() {
            PedidoAluguel pedido = novoPedido(8L, 2L, "X", StatusPedido.APROVADO);
            when(pedidoAluguelRepository.findByIdAndClienteId(8L, 2L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoAluguelService.atualizarPedido(2L, 8L, "Tentativa"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDENTE");

            verify(pedidoAluguelRepository, never()).update(any());
        }

        @Test
        @DisplayName("deve rejeitar quando pedido não existe para o cliente")
        void deveRejeitarQuandoPedidoNaoDoCliente() {
            when(pedidoAluguelRepository.findByIdAndClienteId(8L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoAluguelService.atualizarPedido(2L, 8L, "X"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Pedido não encontrado.");

            verify(pedidoAluguelRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("cancelarPedido")
    class CancelarPedido {

        @Test
        @DisplayName("deve cancelar quando pedido está pendente")
        void deveCancelarQuandoPendente() {
            PedidoAluguel pedido = novoPedido(9L, 3L, "Pedido", StatusPedido.PENDENTE);
            when(pedidoAluguelRepository.findByIdAndClienteId(9L, 3L)).thenReturn(Optional.of(pedido));
            when(pedidoAluguelRepository.update(any(PedidoAluguel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PedidoAluguel cancelado = pedidoAluguelService.cancelarPedido(3L, 9L);

            assertThat(cancelado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
            verify(pedidoAluguelRepository).update(eq(pedido));
        }

        @Test
        @DisplayName("deve bloquear cancelamento quando pedido não está pendente")
        void deveBloquearQuandoNaoPendente() {
            PedidoAluguel pedido = novoPedido(9L, 3L, "Pedido", StatusPedido.REPROVADO);
            when(pedidoAluguelRepository.findByIdAndClienteId(9L, 3L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoAluguelService.cancelarPedido(3L, 9L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDENTE");

            verify(pedidoAluguelRepository, never()).update(any());
        }

        @Test
        @DisplayName("deve rejeitar quando pedido não existe para o cliente")
        void deveRejeitarQuandoPedidoNaoDoCliente() {
            when(pedidoAluguelRepository.findByIdAndClienteId(9L, 3L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoAluguelService.cancelarPedido(3L, 9L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Pedido não encontrado.");

            verify(pedidoAluguelRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("aprovarPedido")
    class AprovarPedido {

        @Test
        @DisplayName("deve aprovar pedido pendente e gerar contrato")
        void deveAprovarPendenteEGerarContrato() {
            PedidoAluguel pedido = novoPedido(20L, 1L, "Descrição longa o suficiente para validação do pedido.", StatusPedido.PENDENTE);
            pedido.setAutomovel(novoAutomovel(77L));
            Contrato contrato = new Contrato();
            contrato.setId(100L);

            when(pedidoAluguelRepository.buscarDetalhePorId(20L)).thenReturn(Optional.of(pedido));
            when(pedidoAluguelRepository.update(any(PedidoAluguel.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(contratoService.criarContratoParaPedidoAprovado(any(PedidoAluguel.class), any(TipoContrato.class))).thenReturn(contrato);

            PedidoAluguel resultado = pedidoAluguelService.aprovarPedido(20L, TipoContrato.LOCACAO_SIMPLES);

            assertThat(resultado.getStatus()).isEqualTo(StatusPedido.APROVADO);
            verify(contratoService).criarContratoParaPedidoAprovado(any(PedidoAluguel.class), eq(TipoContrato.LOCACAO_SIMPLES));
        }

        @Test
        @DisplayName("deve bloquear aprovação quando pedido não está pendente")
        void deveBloquearQuandoNaoPendente() {
            PedidoAluguel pedido = novoPedido(20L, 1L, "Descrição longa o suficiente para validação do pedido.", StatusPedido.APROVADO);
            when(pedidoAluguelRepository.buscarDetalhePorId(20L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoAluguelService.aprovarPedido(20L, TipoContrato.LOCACAO_SIMPLES))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDENTES");

            verify(contratoService, never()).criarContratoParaPedidoAprovado(any(), any(TipoContrato.class));
        }
    }

    @Nested
    @DisplayName("reprovarPedido")
    class ReprovarPedido {

        @Test
        @DisplayName("deve reprovar pedido pendente sem gerar contrato")
        void deveReprovarSemContrato() {
            PedidoAluguel pedido = novoPedido(21L, 1L, "Descrição longa o suficiente para validação do pedido.", StatusPedido.PENDENTE);
            when(pedidoAluguelRepository.buscarDetalhePorId(21L)).thenReturn(Optional.of(pedido));
            when(pedidoAluguelRepository.update(any(PedidoAluguel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PedidoAluguel resultado = pedidoAluguelService.reprovarPedido(21L);

            assertThat(resultado.getStatus()).isEqualTo(StatusPedido.REPROVADO);
            verify(contratoService, never()).criarContratoParaPedidoAprovado(any(), any(TipoContrato.class));
        }

        @Test
        @DisplayName("deve bloquear reprovação quando pedido já foi finalizado")
        void deveBloquearQuandoJaFinalizado() {
            PedidoAluguel pedido = novoPedido(21L, 1L, "Descrição longa o suficiente para validação do pedido.", StatusPedido.REPROVADO);
            when(pedidoAluguelRepository.buscarDetalhePorId(21L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoAluguelService.reprovarPedido(21L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDENTES");

            verify(contratoService, never()).criarContratoParaPedidoAprovado(any(), any(TipoContrato.class));
        }
    }

    private static Cliente novoCliente(Long id, String nome, String cpf) {
        Cliente cliente = new Cliente(nome, cpf, null, "Rua teste", "Dev");
        cliente.setId(id);
        return cliente;
    }

    private static PedidoAluguel novoPedido(Long id, Long clienteId, String descricao, StatusPedido status) {
        PedidoAluguel pedido = new PedidoAluguel(novoCliente(clienteId, "Cliente", "12345678900"), descricao);
        pedido.setId(id);
        pedido.setStatus(status);
        return pedido;
    }

    private static Automovel novoAutomovel(Long id) {
        Automovel a = new Automovel("ABC1D23", "Marca", "Modelo", 2020);
        a.setId(id);
        return a;
    }
}
