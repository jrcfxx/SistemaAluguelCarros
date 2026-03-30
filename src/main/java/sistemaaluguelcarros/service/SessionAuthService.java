package sistemaaluguelcarros.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.session.Session;
import jakarta.inject.Singleton;
import sistemaaluguelcarros.auth.AuthSessionKeys;
import sistemaaluguelcarros.domain.Cliente;

import java.util.Optional;

@Singleton
public class SessionAuthService {

    private final ClienteService clienteService;

    public SessionAuthService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void autenticar(Session session, Cliente cliente) {
        session.put(AuthSessionKeys.CLIENTE_ID, cliente.getId());
    }

    public Optional<Cliente> clienteAutenticado(@Nullable Session session) {
        if (session == null) {
            return Optional.empty();
        }

        Optional<Long> clienteId = session.get(AuthSessionKeys.CLIENTE_ID, Long.class);
        if (clienteId.isEmpty()) {
            return Optional.empty();
        }

        Optional<Cliente> cliente = clienteService.buscarPorId(clienteId.get());
        if (cliente.isEmpty()) {
            session.remove(AuthSessionKeys.CLIENTE_ID);
        }
        return cliente;
    }

    public boolean isAutenticado(@Nullable Session session) {
        return clienteAutenticado(session).isPresent();
    }

    public boolean isClienteDaSessao(@Nullable Session session, Long clienteId) {
        return clienteAutenticado(session)
                .map(cliente -> cliente.getId().equals(clienteId))
                .orElse(false);
    }

    public void limparSessao(@Nullable Session session) {
        if (session != null) {
            session.remove(AuthSessionKeys.CLIENTE_ID);
        }
    }
}
