package sistemaaluguelcarros.service;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import sistemaaluguelcarros.auth.AgenteSessao;

import java.util.Optional;

@Singleton
public class AgenteAuthService {

    private final String usernameConfigurado;
    private final String senhaConfigurada;
    private final String nomeExibicaoConfigurado;

    public AgenteAuthService(
            @Value("${AGENT_USERNAME:agente}") String usernameConfigurado,
            @Value("${AGENT_PASSWORD:agente123}") String senhaConfigurada,
            @Value("${AGENT_DISPLAY_NAME:Agente}") String nomeExibicaoConfigurado
    ) {
        this.usernameConfigurado = normalizar(usernameConfigurado);
        this.senhaConfigurada = senhaConfigurada == null ? "" : senhaConfigurada;
        this.nomeExibicaoConfigurado = nomeExibicaoConfigurado == null || nomeExibicaoConfigurado.isBlank()
                ? "Agente"
                : nomeExibicaoConfigurado.trim();
    }

    public Optional<AgenteSessao> autenticar(String username, String senha) {
        String usernameNormalizado = normalizar(username);
        if (usernameNormalizado.isEmpty() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }
        if (!usernameConfigurado.equalsIgnoreCase(usernameNormalizado)) {
            return Optional.empty();
        }
        if (!senhaConfigurada.equals(senha)) {
            return Optional.empty();
        }
        return Optional.of(new AgenteSessao(usernameConfigurado, nomeExibicaoConfigurado));
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
