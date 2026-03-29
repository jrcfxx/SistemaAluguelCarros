package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.Optional;

@Singleton
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente salvar(@Valid Cliente cliente) {
        if (cliente.getId() == null) {
            Optional<Cliente> existente = clienteRepository.findByCpf(cliente.getCpf());
            if (existente.isPresent()) {
                throw new IllegalStateException("CPF já cadastrado: " + cliente.getCpf());
            }
            return clienteRepository.save(cliente);
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
