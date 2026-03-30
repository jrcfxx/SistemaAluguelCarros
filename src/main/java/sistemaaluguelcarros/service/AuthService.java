package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.Optional;

@Singleton
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica por CPF e senha. Não distingue CPF inexistente de senha inválida (retorno vazio).
     */
    public Optional<Cliente> autenticar(String cpf, String senhaPlana) {
        if (cpf == null || cpf.isBlank() || senhaPlana == null) {
            return Optional.empty();
        }
        String cpfNorm = cpf.trim();
        Optional<Cliente> opt = clienteRepository.findByCpf(cpfNorm);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Cliente cliente = opt.get();
        String hash = cliente.getSenhaHash();
        if (hash == null || hash.isBlank() || !passwordEncoder.matches(senhaPlana, hash)) {
            return Optional.empty();
        }
        return Optional.of(cliente);
    }
}
