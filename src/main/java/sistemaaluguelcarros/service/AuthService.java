package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.util.Optional;

@Singleton
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordHashService passwordHashService;

    public AuthService(ClienteRepository clienteRepository, PasswordHashService passwordHashService) {
        this.clienteRepository = clienteRepository;
        this.passwordHashService = passwordHashService;
    }

    /**
     * Autentica por CPF e senha. Não distingue CPF inexistente de senha inválida (retorno vazio).
     */
    public Optional<Cliente> autenticar(String cpf, String senhaPlana) {
        if (cpf == null || cpf.isBlank() || senhaPlana == null || senhaPlana.isBlank()) {
            return Optional.empty();
        }
        if (!ValidationRules.isCpfValido(cpf)) {
            return Optional.empty();
        }
        String cpfNorm = ValidationRules.normalizarCpf(cpf);
        Optional<Cliente> opt = clienteRepository.findByCpf(cpfNorm);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Cliente cliente = opt.get();
        String hash = cliente.getSenhaHash();
        if (!passwordHashService.matches(senhaPlana, hash)) {
            return Optional.empty();
        }
        return Optional.of(cliente);
    }
}
