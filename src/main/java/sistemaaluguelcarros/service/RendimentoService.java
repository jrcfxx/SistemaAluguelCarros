package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Empregador;
import sistemaaluguelcarros.domain.Rendimento;
import sistemaaluguelcarros.repository.RendimentoRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.math.BigDecimal;
import java.util.List;
@Singleton
public class RendimentoService {

    private final RendimentoRepository rendimentoRepository;
    private final ClienteService clienteService;

    public RendimentoService(RendimentoRepository rendimentoRepository, ClienteService clienteService) {
        this.rendimentoRepository = rendimentoRepository;
        this.clienteService = clienteService;
    }

    public List<Rendimento> listarPorCliente(Long clienteId) {
        return rendimentoRepository.listarComEmpregadorPorCliente(clienteId);
    }

    public Rendimento adicionar(Long clienteId, String nomeEmpregador, String cnpjBruto, BigDecimal valorMensal) {
        if (rendimentoRepository.countByCliente_Id(clienteId) >= ValidationRules.MAX_RENDIMENTOS_POR_CLIENTE) {
            throw new IllegalStateException(
                    "Cada cliente pode cadastrar no máximo " + ValidationRules.MAX_RENDIMENTOS_POR_CLIENTE + " rendimentos."
            );
        }

        ValidationRules.validarNomeEmpregador(nomeEmpregador).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        ValidationRules.validarCnpjOpcional(cnpjBruto).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        ValidationRules.validarValorRendimento(valorMensal).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });

        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado."));

        Empregador empregador = new Empregador();
        empregador.setNome(nomeEmpregador.trim());
        empregador.setCnpj(ValidationRules.normalizarCnpj(cnpjBruto));

        Rendimento rendimento = new Rendimento();
        rendimento.setCliente(cliente);
        rendimento.setEmpregador(empregador);
        rendimento.setValorMensal(valorMensal);

        return rendimentoRepository.save(rendimento);
    }

    public void excluir(Long clienteId, Long rendimentoId) {
        Rendimento rendimento = rendimentoRepository.findByIdAndCliente_Id(rendimentoId, clienteId)
                .orElseThrow(() -> new IllegalStateException("Rendimento não encontrado ou não pertence a este cliente."));
        rendimentoRepository.delete(rendimento);
    }

    public long contarPorCliente(Long clienteId) {
        return rendimentoRepository.countByCliente_Id(clienteId);
    }

    public boolean clientePodeAdicionar(Long clienteId) {
        return contarPorCliente(clienteId) < ValidationRules.MAX_RENDIMENTOS_POR_CLIENTE;
    }
}
