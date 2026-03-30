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

    private String normalizarDescricao(String descricaoSolicitacao) {
        return descricaoSolicitacao == null ? "" : descricaoSolicitacao.trim();
    }
}
