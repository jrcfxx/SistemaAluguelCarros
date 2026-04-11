package sistemaaluguelcarros.service;

import io.micronaut.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.auth.AuthSessionKeys;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgenteSessionService")
class AgenteSessionServiceTest {

    @Mock
    private Session session;

    private AgenteSessionService agenteSessionService;

    @BeforeEach
    void setUp() {
        agenteSessionService = new AgenteSessionService();
    }

    @Test
    @DisplayName("deve retornar agente autenticado quando sessão é válida")
    void deveRetornarAgenteAutenticadoQuandoSessaoValida() {
        when(session.get(AuthSessionKeys.AGENTE_USERNAME, String.class)).thenReturn(Optional.of("analista"));
        when(session.get(AuthSessionKeys.AGENTE_NOME_EXIBICAO, String.class)).thenReturn(Optional.of("Agente Financeiro"));

        Optional<AgenteSessao> resultado = agenteSessionService.agenteAutenticado(session);

        assertThat(resultado).contains(new AgenteSessao("analista", "Agente Financeiro"));
    }

    @Test
    @DisplayName("deve limpar sessão incompleta")
    void deveLimparSessaoIncompleta() {
        when(session.get(AuthSessionKeys.AGENTE_USERNAME, String.class)).thenReturn(Optional.of("analista"));
        when(session.get(AuthSessionKeys.AGENTE_NOME_EXIBICAO, String.class)).thenReturn(Optional.empty());

        Optional<AgenteSessao> resultado = agenteSessionService.agenteAutenticado(session);

        assertThat(resultado).isEmpty();
        verify(session).remove(AuthSessionKeys.AGENTE_USERNAME);
        verify(session).remove(AuthSessionKeys.AGENTE_NOME_EXIBICAO);
    }

    @Test
    @DisplayName("deve retornar vazio quando sessão não existe")
    void deveRetornarVazioQuandoSessaoNaoExiste() {
        Optional<AgenteSessao> resultado = agenteSessionService.agenteAutenticado(null);

        assertThat(resultado).isEmpty();
        verifyNoInteractions(session);
    }
}
