package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.repository.PedidoAluguelRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class PedidoAluguelService {

    private final PedidoAluguelRepository pedidoAluguelRepository;
    private final ClienteService clienteService;

    public PedidoAluguelService(
            PedidoAluguelRepository pedidoAluguelRepository,
            ClienteService clienteService
    ) {
        this.pedidoAluguelRepository = pedidoAluguelRepository;
        this.clienteService = clienteService;
    }

    public PedidoAluguel criarPedido(Long clienteId, String descricaoSolicitacao) {
        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado."));

        String descricaoNormalizada = normalizarDescricao(descricaoSolicitacao);
        if (descricaoNormalizada.isEmpty()) {
            throw new IllegalStateException("Descrição da solicitação é obrigatória.");
        }

        LocalDateTime agora = LocalDateTime.now();
        PedidoAluguel pedido = new PedidoAluguel(cliente, descricaoNormalizada);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataSolicitacao(agora);
        pedido.setUltimaAtualizacao(agora);

        return pedidoAluguelRepository.save(pedido);
    }

    public Optional<PedidoAluguel> buscarPorId(Long id) {
        return pedidoAluguelRepository.findById(id);
    }

    public Optional<PedidoAluguel> buscarPorIdECliente(Long id, Long clienteId) {
        return pedidoAluguelRepository.findByIdAndClienteId(id, clienteId);
    }

    public List<PedidoAluguel> listarPorCliente(Long clienteId) {
        return pedidoAluguelRepository.findByClienteIdOrderByDataSolicitacaoDesc(clienteId);
    }

    /**
     * Atualiza a descrição de um pedido do cliente. Somente {@link StatusPedido#PENDENTE}.
     */
    public PedidoAluguel atualizarPedido(Long clienteId, Long pedidoId, String descricaoSolicitacao) {
        PedidoAluguel pedido = pedidoAluguelRepository.findByIdAndClienteId(pedidoId, clienteId)
                .orElseThrow(() -> new IllegalStateException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Só é possível editar pedidos com status PENDENTE. Status atual: " + pedido.getStatus() + "."
            );
        }

        String descricaoNormalizada = normalizarDescricao(descricaoSolicitacao);
        if (descricaoNormalizada.isEmpty()) {
            throw new IllegalStateException("Descrição da solicitação é obrigatória.");
        }

        pedido.setDescricaoSolicitacao(descricaoNormalizada);
        return pedidoAluguelRepository.update(pedido);
    }

    /**
     * Cancela um pedido do cliente (status {@link StatusPedido#CANCELADO}). Somente {@link StatusPedido#PENDENTE}.
     */
    public PedidoAluguel cancelarPedido(Long clienteId, Long pedidoId) {
        PedidoAluguel pedido = pedidoAluguelRepository.findByIdAndClienteId(pedidoId, clienteId)
                .orElseThrow(() -> new IllegalStateException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Só é possível cancelar pedidos com status PENDENTE. Status atual: " + pedido.getStatus() + "."
            );
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoAluguelRepository.update(pedido);
    }

    private String normalizarDescricao(String descricaoSolicitacao) {
        return descricaoSolicitacao == null ? "" : descricaoSolicitacao.trim();
    }
}
