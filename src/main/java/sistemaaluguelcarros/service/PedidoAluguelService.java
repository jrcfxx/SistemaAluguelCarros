package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.repository.PedidoAluguelRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class PedidoAluguelService {

    private final PedidoAluguelRepository pedidoAluguelRepository;
    private final ClienteService clienteService;
    private final ContratoService contratoService;
    private final AutomovelRepository automovelRepository;

    public PedidoAluguelService(
            PedidoAluguelRepository pedidoAluguelRepository,
            ClienteService clienteService,
            ContratoService contratoService,
            AutomovelRepository automovelRepository
    ) {
        this.pedidoAluguelRepository = pedidoAluguelRepository;
        this.clienteService = clienteService;
        this.contratoService = contratoService;
        this.automovelRepository = automovelRepository;
    }

    public PedidoAluguel criarPedido(Long clienteId, Long automovelId, String descricaoSolicitacao) {
        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado."));
        if (automovelId == null) {
            throw new IllegalStateException("Selecione o automóvel desejado no pedido.");
        }
        Automovel automovel = automovelRepository.findById(automovelId)
                .orElseThrow(() -> new IllegalStateException("Automóvel não encontrado."));

        String descricaoNormalizada = normalizarDescricao(descricaoSolicitacao);
        ValidationRules.validarDescricaoPedido(descricaoNormalizada).ifPresent(mensagem -> {
            throw new IllegalStateException(mensagem);
        });

        LocalDateTime agora = LocalDateTime.now();
        PedidoAluguel pedido = new PedidoAluguel(cliente, descricaoNormalizada);
        pedido.setAutomovel(automovel);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataSolicitacao(agora);
        pedido.setUltimaAtualizacao(agora);

        return pedidoAluguelRepository.save(pedido);
    }

    public Optional<PedidoAluguel> buscarPorId(Long id) {
        return pedidoAluguelRepository.findById(id);
    }

    public List<PedidoAluguel> listarParaAnalise() {
        return pedidoAluguelRepository.listarParaAnalise();
    }

    public long contarPorStatus(StatusPedido status) {
        return pedidoAluguelRepository.countByStatus(status);
    }

    public long contarTodos() {
        return pedidoAluguelRepository.count();
    }

    public Optional<PedidoAluguel> buscarDetalheParaAnalise(Long id) {
        return pedidoAluguelRepository.buscarDetalhePorId(id);
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
        ValidationRules.validarDescricaoPedido(descricaoNormalizada).ifPresent(mensagem -> {
            throw new IllegalStateException(mensagem);
        });

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

    /**
     * Aprova o pedido (fluxo do agente), gera contrato e atualiza o status. Somente {@link StatusPedido#PENDENTE}.
     */
    @Transactional
    public PedidoAluguel aprovarPedido(Long pedidoId, TipoContrato tipoContrato) {
        PedidoAluguel pedido = pedidoAluguelRepository.buscarDetalhePorId(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Apenas pedidos PENDENTES podem ser aprovados. Status atual: " + pedido.getStatus() + "."
            );
        }
        if (pedido.getAutomovel() == null) {
            throw new IllegalStateException(
                    "Pedido sem automóvel vinculado. Inclua um veículo no pedido antes de aprovar."
            );
        }

        pedido.setStatus(StatusPedido.APROVADO);
        PedidoAluguel atualizado = pedidoAluguelRepository.update(pedido);
        TipoContrato tipo = tipoContrato != null ? tipoContrato : TipoContrato.LOCACAO_SIMPLES;
        Contrato contrato = contratoService.criarContratoParaPedidoAprovado(atualizado, tipo);
        atualizado.setContrato(contrato);
        return atualizado;
    }

    /**
     * Reprova o pedido (fluxo do agente). Somente {@link StatusPedido#PENDENTE}. Não gera contrato.
     */
    @Transactional
    public PedidoAluguel reprovarPedido(Long pedidoId) {
        PedidoAluguel pedido = pedidoAluguelRepository.buscarDetalhePorId(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Apenas pedidos PENDENTES podem ser reprovados. Status atual: " + pedido.getStatus() + "."
            );
        }

        pedido.setStatus(StatusPedido.REPROVADO);
        return pedidoAluguelRepository.update(pedido);
    }

    private String normalizarDescricao(String descricaoSolicitacao) {
        return descricaoSolicitacao == null ? "" : descricaoSolicitacao.trim();
    }
}
