package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.Optional;

@Singleton
public class ClienteService {

    private static final int SENHA_MIN_LENGTH = 6;

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cadastro inicial com senha (hash). Novos clientes devem usar este método.
     */
    public Cliente cadastrarComSenha(Cliente cliente, String senhaPlana, String confirmacaoSenha) {
        if (cliente.getId() != null) {
            throw new IllegalStateException("Para atualizar um cliente existente, use salvar.");
        }
        if (senhaPlana == null || senhaPlana.isBlank()) {
            throw new IllegalStateException("Senha é obrigatória.");
        }
        if (senhaPlana.length() < SENHA_MIN_LENGTH) {
            throw new IllegalStateException("A senha deve ter no mínimo " + SENHA_MIN_LENGTH + " caracteres.");
        }
        if (!senhaPlana.equals(confirmacaoSenha)) {
            throw new IllegalStateException("A confirmação de senha não confere.");
        }

        Optional<Cliente> existente = clienteRepository.findByCpf(cliente.getCpf());
        if (existente.isPresent()) {
            throw new IllegalStateException("Este CPF já está cadastrado. Use outro CPF ou faça login.");
        }

        cliente.setSenhaHash(passwordEncoder.encode(senhaPlana));
        return clienteRepository.save(cliente);
    }

    /**
     * Atualização de cliente já existente (mantém o hash da senha salvo na entidade).
     */
    public Cliente salvar(@Valid Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalStateException("Cadastro de novos clientes deve usar cadastrarComSenha.");
        }

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
        return clienteRepository.findByCpf(cpf);
    }

    public Iterable<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalStateException("Cliente não encontrado.");
        }
        clienteRepository.deleteById(id);
    }
}
