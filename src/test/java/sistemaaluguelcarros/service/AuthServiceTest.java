package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private PasswordHashService passwordHashService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(clienteRepository, passwordHashService);
    }

    @Nested
    @DisplayName("autenticar")
    class Autenticar {

        @Test
        @DisplayName("deve autenticar quando CPF e senha conferem")
        void deveAutenticarComSucesso() {
            Cliente cliente = new Cliente("Ana", "52998224725", null, "Rua A, 10", null);
            cliente.setId(1L);
            cliente.setSenhaHash(HASH);

            when(clienteRepository.findByCpf("52998224725")).thenReturn(Optional.of(cliente));
            when(passwordHashService.matches("minhasenha", HASH)).thenReturn(true);

            Optional<Cliente> resultado = authService.autenticar("529.982.247-25", "minhasenha");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo(1L);
            verify(passwordHashService).matches("minhasenha", HASH);
        }

        @Test
        @DisplayName("deve retornar vazio quando senha não confere")
        void deveFalharQuandoSenhaInvalida() {
            Cliente cliente = new Cliente("Ana", "52998224725", null, "Rua A, 10", null);
            cliente.setSenhaHash(HASH);

            when(clienteRepository.findByCpf("52998224725")).thenReturn(Optional.of(cliente));
            when(passwordHashService.matches("errada", HASH)).thenReturn(false);

            Optional<Cliente> resultado = authService.autenticar("529.982.247-25", "errada");

            assertThat(resultado).isEmpty();
            verify(passwordHashService).matches("errada", HASH);
        }

        @Test
        @DisplayName("deve retornar vazio quando CPF não existe")
        void deveFalharQuandoCpfNaoExiste() {
            when(clienteRepository.findByCpf("12345678909")).thenReturn(Optional.empty());

            Optional<Cliente> resultado = authService.autenticar("123.456.789-09", "qualquer");

            assertThat(resultado).isEmpty();
            verifyNoInteractions(passwordHashService);
        }

        @Test
        @DisplayName("deve retornar vazio quando senha está em branco")
        void deveFalharQuandoSenhaEstaEmBranco() {
            Optional<Cliente> resultado = authService.autenticar("529.982.247-25", "   ");

            assertThat(resultado).isEmpty();
            verifyNoInteractions(clienteRepository, passwordHashService);
        }

        @Test
        @DisplayName("deve retornar vazio quando CPF é inválido")
        void deveFalharQuandoCpfInvalido() {
            Optional<Cliente> resultado = authService.autenticar("111.111.111-11", "qualquer123");

            assertThat(resultado).isEmpty();
            verifyNoInteractions(clienteRepository, passwordHashService);
        }
    }
}
