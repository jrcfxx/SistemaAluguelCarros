package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;

/**
 * Atualiza a titularidade do {@link Automovel} conforme o tipo de contrato gerado (sem histórico; apenas estado atual).
 */
@Singleton
public class PropriedadeVeiculoService {

    private final AutomovelRepository automovelRepository;

    public PropriedadeVeiculoService(AutomovelRepository automovelRepository) {
        this.automovelRepository = automovelRepository;
    }

    @Transactional
    public Automovel aplicarPropriedadeAposContrato(Contrato contrato) {
        PedidoAluguel pedido = contrato.getPedido();
        if (pedido == null) {
            throw new IllegalStateException("Contrato sem pedido vinculado.");
        }
        Automovel automovel = pedido.getAutomovel();
        if (automovel == null) {
            throw new IllegalStateException("Pedido sem automóvel vinculado; não é possível atualizar a propriedade.");
        }
        TipoContrato tipo = contrato.getTipoContrato();
        Cliente cliente = pedido.getCliente();

        switch (tipo) {
            case LOCACAO_SIMPLES -> aplicarLocacaoSimples(automovel);
            case LOCACAO_COM_OPCAO_COMPRA -> aplicarOpcaoCompra(automovel, cliente);
            case CREDITO_BANCARIO -> aplicarCreditoBancario(automovel);
        }

        return automovelRepository.update(automovel);
    }

    private static void aplicarLocacaoSimples(Automovel automovel) {
        automovel.setTipoProprietario(TipoProprietarioVeiculo.LOCADORA);
        automovel.setProprietarioCliente(null);
    }

    private static void aplicarOpcaoCompra(Automovel automovel, Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalStateException("Cliente do pedido inválido para transferência de propriedade.");
        }
        automovel.setTipoProprietario(TipoProprietarioVeiculo.CLIENTE);
        automovel.setProprietarioCliente(cliente);
    }

    private static void aplicarCreditoBancario(Automovel automovel) {
        automovel.setTipoProprietario(TipoProprietarioVeiculo.BANCO);
        automovel.setProprietarioCliente(null);
    }
}
