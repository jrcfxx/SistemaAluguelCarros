package sistemaaluguelcarros.service;

import io.micronaut.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.auth.AuthSessionKeys;
import sistemaaluguelcarros.domain.Cliente;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionAuthService")
class SessionAuthServiceTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private Session session;

    private SessionAuthService sessionAuthService;

    @BeforeEach
    void setUp() {
        sessionAuthService = new SessionAuthService(clienteService);
    }

    @Test
    @DisplayName("deve retornar cliente autenticado quando sessão é válida")
    void deveRetornarClienteAutenticadoQuandoSessaoValida() {
        Cliente cliente = new Cliente("Julia", "12345678900", null, "Rua A", "Dev");
        cliente.setId(1L);

        when(session.get(AuthSessionKeys.CLIENTE_ID, Long.class)).thenReturn(Optional.of(1L));
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = sessionAuthService.clienteAutenticado(session);

        assertThat(resultado).contains(cliente);
    }

    @Test
    @DisplayName("deve limpar sessão quando cliente não existe mais")
    void deveLimparSessaoQuandoClienteNaoExisteMais() {
        when(session.get(AuthSessionKeys.CLIENTE_ID, Long.class)).thenReturn(Optional.of(99L));
        when(clienteService.buscarPorId(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = sessionAuthService.clienteAutenticado(session);

        assertThat(resultado).isEmpty();
        verify(session).remove(AuthSessionKeys.CLIENTE_ID);
    }

    @Test
    @DisplayName("deve retornar vazio quando sessão não existe")
    void deveRetornarVazioQuandoSessaoNaoExiste() {
        Optional<Cliente> resultado = sessionAuthService.clienteAutenticado(null);

        assertThat(resultado).isEmpty();
        verifyNoInteractions(clienteService, session);
    }
}
