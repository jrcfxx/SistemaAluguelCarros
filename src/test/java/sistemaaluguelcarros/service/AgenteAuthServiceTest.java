package sistemaaluguelcarros.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sistemaaluguelcarros.auth.AgenteSessao;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AgenteAuthService")
class AgenteAuthServiceTest {

    @Test
    @DisplayName("deve autenticar agente com credenciais válidas")
    void deveAutenticarAgenteComCredenciaisValidas() {
        AgenteAuthService service = new AgenteAuthService("analista", "segredo", "Agente Financeiro");

        Optional<AgenteSessao> resultado = service.autenticar("analista", "segredo");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().nomeExibicao()).isEqualTo("Agente Financeiro");
    }

    @Test
    @DisplayName("deve rejeitar agente com senha inválida")
    void deveRejeitarAgenteComSenhaInvalida() {
        AgenteAuthService service = new AgenteAuthService("analista", "segredo", "Agente Financeiro");

        Optional<AgenteSessao> resultado = service.autenticar("analista", "errada");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("deve rejeitar agente com usuário em branco")
    void deveRejeitarAgenteComUsuarioEmBranco() {
        AgenteAuthService service = new AgenteAuthService("analista", "segredo", "Agente Financeiro");

        Optional<AgenteSessao> resultado = service.autenticar("   ", "segredo");

        assertThat(resultado).isEmpty();
    }
}
