package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.repository.ClienteRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.util.Optional;

@Singleton
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordHashService passwordHashService;
    private final AutomovelRepository automovelRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordHashService passwordHashService,
            AutomovelRepository automovelRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.passwordHashService = passwordHashService;
        this.automovelRepository = automovelRepository;
    }

    /**
     * Cadastro inicial com senha (hash). Novos clientes devem usar este método.
     */
    public Cliente cadastrarComSenha(Cliente cliente, String senhaPlana, String confirmacaoSenha) {
        if (cliente.getId() != null) {
            throw new IllegalStateException("Para atualizar um cliente existente, use salvar.");
        }
        ValidationRules.validarCliente(cliente).ifPresent(mensagem -> {
            throw new IllegalStateException(mensagem);
        });
        normalizarCpf(cliente);
        ValidationRules.validarSenhaCadastro(senhaPlana, confirmacaoSenha).ifPresent(mensagem -> {
            throw new IllegalStateException(mensagem);
        });

        Optional<Cliente> existente = clienteRepository.findByCpf(cliente.getCpf());
        if (existente.isPresent()) {
            throw new IllegalStateException("Este CPF já está cadastrado. Use outro CPF ou faça login.");
        }

        cliente.setSenhaHash(passwordHashService.hash(senhaPlana));
        return clienteRepository.save(cliente);
    }

    /**
     * Atualização de cliente já existente (mantém o hash da senha salvo na entidade).
     */
    public Cliente salvar(@Valid Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalStateException("Cadastro de novos clientes deve usar cadastrarComSenha.");
        }
        ValidationRules.validarCliente(cliente).ifPresent(mensagem -> {
            throw new IllegalStateException(mensagem);
        });
        normalizarCpf(cliente);

        Optional<Cliente> existente = clienteRepository.findByCpf(cliente.getCpf());
        if (existente.isPresent() && !existente.get().getId().equals(cliente.getId())) {
            throw new IllegalStateException("CPF já cadastrado: " + cliente.getCpf());
        }
        if (!clienteRepository.existsById(cliente.getId())) {
            throw new IllegalStateException("Cliente não encontrado.");
        }

        return clienteRepository.update(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(normalizarCpf(cpf));
    }

    public Iterable<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado."));
        for (Automovel automovel : automovelRepository.findByProprietarioCliente_Id(id)) {
            if (automovel.getTipoProprietario() == TipoProprietarioVeiculo.CLIENTE) {
                automovel.setTipoProprietario(TipoProprietarioVeiculo.LOCADORA);
            }
            automovel.setProprietarioCliente(null);
            automovelRepository.update(automovel);
        }
        clienteRepository.delete(cliente);
    }

    private void normalizarCpf(Cliente cliente) {
        cliente.setCpf(normalizarCpf(cliente.getCpf()));
    }

    private String normalizarCpf(String cpf) {
        return ValidationRules.normalizarCpf(cpf);
    }
}
