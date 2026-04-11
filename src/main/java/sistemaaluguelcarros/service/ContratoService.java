package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.repository.ContratoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Singleton
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final PropriedadeVeiculoService propriedadeVeiculoService;

    public ContratoService(ContratoRepository contratoRepository, PropriedadeVeiculoService propriedadeVeiculoService) {
        this.contratoRepository = contratoRepository;
        this.propriedadeVeiculoService = propriedadeVeiculoService;
    }

    /**
     * Cria e persiste o contrato para um pedido já salvo como {@code APROVADO} e aplica a regra de propriedade do veículo.
     */
    @Transactional
    public Contrato criarContratoParaPedidoAprovado(PedidoAluguel pedidoAprovado, TipoContrato tipoContrato) {
        if (pedidoAprovado.getId() == null) {
            throw new IllegalStateException("Pedido inválido para geração de contrato.");
        }
        if (contratoRepository.findByPedidoId(pedidoAprovado.getId()).isPresent()) {
            throw new IllegalStateException("Já existe contrato registrado para este pedido.");
        }

        LocalDateTime agora = LocalDateTime.now();
        String numero = gerarNumeroContrato(pedidoAprovado.getId(), agora);
        TipoContrato tipo = tipoContrato != null ? tipoContrato : TipoContrato.LOCACAO_SIMPLES;
        String termos = montarTermosAcademicos(pedidoAprovado, numero, agora, tipo);

        Contrato contrato = new Contrato(pedidoAprovado, numero, termos, agora, tipo);
        Contrato salvo = contratoRepository.save(contrato);
        pedidoAprovado.setContrato(salvo);
        propriedadeVeiculoService.aplicarPropriedadeAposContrato(salvo);
        return salvo;
    }

    public Optional<Contrato> buscarParaExibicaoAgente(Long contratoId) {
        return contratoRepository.buscarPorIdComPedidoECliente(contratoId);
    }

    public Optional<Contrato> buscarPorPedidoDoCliente(Long pedidoId, Long clienteId) {
        return contratoRepository.buscarPorPedidoIdEClienteId(pedidoId, clienteId);
    }

    private static String gerarNumeroContrato(Long pedidoId, LocalDateTime momento) {
        String sufixo = momento.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "CTR-" + pedidoId + "-" + sufixo;
    }

    private static String montarTermosAcademicos(
            PedidoAluguel pedido,
            String numeroContrato,
            LocalDateTime data,
            TipoContrato tipoContrato
    ) {
        Cliente c = pedido.getCliente();
        String nomeCliente = c.getNome() != null ? c.getNome() : "";
        String cpf = c.getCpf() != null ? c.getCpf() : "";
        String dataFmt = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String veiculo = "não informado";
        if (pedido.getAutomovel() != null) {
            var a = pedido.getAutomovel();
            veiculo = a.getMarca() + " " + a.getModelo() + " (" + a.getAno() + "), placa " + a.getPlacaNormalizada();
        }

        return """
                CONTRATO DE LOCAÇÃO DE VEÍCULO (VERSÃO ACADÊMICA)

                Número do contrato: %s
                Data de geração: %s
                Tipo de contrato: %s

                Pelo presente instrumento, formaliza-se a intenção de locação decorrente do pedido nº %s,
                registrado no sistema, com a seguinte descrição da solicitação do cliente:

                %s

                Veículo vinculado: %s.

                Dados do cliente locatário: %s, CPF %s.

                Este documento foi gerado automaticamente após aprovação do pedido pelo agente e tem caráter
                demonstrativo para fins de laboratório, sem efeitos legais ou financeiros reais.

                Locadora (referência de sistema): Sistema de Aluguel de Carros — projeto acadêmico.
                """.formatted(
                numeroContrato,
                dataFmt,
                tipoContrato.getTituloAmigavel(),
                pedido.getId(),
                pedido.getDescricaoSolicitacao(),
                veiculo,
                nomeCliente,
                cpf
        ).trim();
    }
}
