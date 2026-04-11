package sistemaaluguelcarros.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.session.Session;
import jakarta.inject.Singleton;
import sistemaaluguelcarros.auth.AgenteSessao;
import sistemaaluguelcarros.auth.AuthSessionKeys;

import java.util.Optional;

@Singleton
public class AgenteSessionService {

    public void autenticar(Session session, AgenteSessao agenteSessao) {
        session.put(AuthSessionKeys.AGENTE_USERNAME, agenteSessao.username());
        session.put(AuthSessionKeys.AGENTE_NOME_EXIBICAO, agenteSessao.nomeExibicao());
    }

    public Optional<AgenteSessao> agenteAutenticado(@Nullable Session session) {
        if (session == null) {
            return Optional.empty();
        }

        Optional<String> username = session.get(AuthSessionKeys.AGENTE_USERNAME, String.class);
        Optional<String> nomeExibicao = session.get(AuthSessionKeys.AGENTE_NOME_EXIBICAO, String.class);
        if (username.isEmpty() || nomeExibicao.isEmpty()) {
            limparSessao(session);
            return Optional.empty();
        }

        return Optional.of(new AgenteSessao(username.get(), nomeExibicao.get()));
    }

    public boolean isAutenticado(@Nullable Session session) {
        return agenteAutenticado(session).isPresent();
    }

    public void limparSessao(@Nullable Session session) {
        if (session != null) {
            session.remove(AuthSessionKeys.AGENTE_USERNAME);
            session.remove(AuthSessionKeys.AGENTE_NOME_EXIBICAO);
        }
    }
}
