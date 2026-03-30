package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    private static final String HASH = "$2a$10$hashsalvobanco";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(clienteRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("autenticar")
    class Autenticar {

        @Test
        @DisplayName("deve autenticar quando CPF e senha conferem")
        void deveAutenticarComSucesso() {
            Cliente cliente = new Cliente("Ana", "123.456.789-00", null, "Rua A", null);
            cliente.setId(1L);
            cliente.setSenhaHash(HASH);

            when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(cliente));
            when(passwordEncoder.matches("minhasenha", HASH)).thenReturn(true);

            Optional<Cliente> resultado = authService.autenticar("123.456.789-00", "minhasenha");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo(1L);
            verify(passwordEncoder).matches("minhasenha", HASH);
        }

        @Test
        @DisplayName("deve retornar vazio quando senha não confere")
        void deveFalharQuandoSenhaInvalida() {
            Cliente cliente = new Cliente("Ana", "123.456.789-00", null, "Rua A", null);
            cliente.setSenhaHash(HASH);

            when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(cliente));
            when(passwordEncoder.matches("errada", HASH)).thenReturn(false);

            Optional<Cliente> resultado = authService.autenticar("123.456.789-00", "errada");

            assertThat(resultado).isEmpty();
            verify(passwordEncoder).matches("errada", HASH);
        }

        @Test
        @DisplayName("deve retornar vazio quando CPF não existe")
        void deveFalharQuandoCpfNaoExiste() {
            when(clienteRepository.findByCpf("000.000.000-00")).thenReturn(Optional.empty());

            Optional<Cliente> resultado = authService.autenticar("000.000.000-00", "qualquer");

            assertThat(resultado).isEmpty();
            verifyNoInteractions(passwordEncoder);
        }
    }
}
