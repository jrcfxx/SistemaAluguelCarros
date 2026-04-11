package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.repository.ContratoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Singleton
public class ContratoService {

    private final ContratoRepository contratoRepository;

    public ContratoService(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    /**
     * Cria e persiste o contrato para um pedido já salvo como {@code APROVADO}.
     */
    public Contrato criarContratoParaPedidoAprovado(PedidoAluguel pedidoAprovado) {
        if (pedidoAprovado.getId() == null) {
            throw new IllegalStateException("Pedido inválido para geração de contrato.");
        }
        if (contratoRepository.findByPedidoId(pedidoAprovado.getId()).isPresent()) {
            throw new IllegalStateException("Já existe contrato registrado para este pedido.");
        }

        LocalDateTime agora = LocalDateTime.now();
        String numero = gerarNumeroContrato(pedidoAprovado.getId(), agora);
        String termos = montarTermosAcademicos(pedidoAprovado, numero, agora);

        Contrato contrato = new Contrato(pedidoAprovado, numero, termos, agora);
        Contrato salvo = contratoRepository.save(contrato);
        pedidoAprovado.setContrato(salvo);
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

    private static String montarTermosAcademicos(PedidoAluguel pedido, String numeroContrato, LocalDateTime data) {
        Cliente c = pedido.getCliente();
        String nomeCliente = c.getNome() != null ? c.getNome() : "";
        String cpf = c.getCpf() != null ? c.getCpf() : "";
        String dataFmt = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return """
                CONTRATO DE LOCAÇÃO DE VEÍCULO (VERSÃO ACADÊMICA)

                Número do contrato: %s
                Data de geração: %s

                Pelo presente instrumento, formaliza-se a intenção de locação decorrente do pedido nº %s,
                registrado no sistema, com a seguinte descrição da solicitação do cliente:

                %s

                Dados do cliente locatário: %s, CPF %s.

                Este documento foi gerado automaticamente após aprovação do pedido pelo agente e tem caráter
                demonstrativo para fins de laboratório, sem efeitos legais ou financeiros reais.

                Locadora (referência de sistema): Sistema de Aluguel de Carros — projeto acadêmico.
                """.formatted(
                numeroContrato,
                dataFmt,
                pedido.getId(),
                pedido.getDescricaoSolicitacao(),
                nomeCliente,
                cpf
        ).trim();
    }
}
