package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.repository.PedidoAluguelRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    private PedidoAluguelService pedidoAluguelService;

    @BeforeEach
    void setUp() {
        pedidoAluguelService = new PedidoAluguelService(pedidoAluguelRepository, clienteService);
    }

    @Nested
    @DisplayName("criarPedido")
    class CriarPedido {

        @Test
        @DisplayName("deve criar pedido com status pendente")
        void deveCriarPedidoComStatusPendente() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
            when(pedidoAluguelRepository.save(any(PedidoAluguel.class))).thenAnswer(invocation -> {
                PedidoAluguel pedido = invocation.getArgument(0);
                pedido.setId(10L);
                return pedido;
            });

            PedidoAluguel pedido = pedidoAluguelService.criarPedido(1L, " Aluguel de veículo para uso urbano ");

            assertThat(pedido.getId()).isEqualTo(10L);
            assertThat(pedido.getCliente().getId()).isEqualTo(1L);
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

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(99L, "Pedido teste"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente não encontrado.");

            verify(pedidoAluguelRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve rejeitar pedido sem descrição")
        void deveRejeitarQuandoDescricaoVazia() {
            Cliente cliente = novoCliente(1L, "Julia Fiorini", "14434366661");
            when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> pedidoAluguelService.criarPedido(1L, "   "))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Descrição da solicitação é obrigatória.");

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
}
