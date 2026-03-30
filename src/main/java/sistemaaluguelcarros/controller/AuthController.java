package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AuthSessionKeys;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.AuthService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private static final String MSG_LOGIN_INVALIDO = "CPF ou senha inválidos. Verifique os dados e tente novamente.";

    private final AuthService authService;
    private final SessionStore<Session> sessionStore;

    public AuthController(AuthService authService, SessionStore<Session> sessionStore) {
        this.authService = authService;
        this.sessionStore = sessionStore;
    }

    @Get("/login")
    public ModelAndView<Map<String, Object>> loginForm(
            @Nullable @QueryValue String erro,
            @Nullable @QueryValue String mensagem
    ) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("erro", erro);
        model.put("mensagem", mensagem);
        return new ModelAndView<>("auth/login", model);
    }

    @Post(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Object login(String cpf, String senha, Session session) {
        Optional<Cliente> cliente = authService.autenticar(cpf, senha);
        if (cliente.isEmpty()) {
            URI uri = UriBuilder.of("/login")
                    .queryParam("erro", MSG_LOGIN_INVALIDO)
                    .build();
            return HttpResponse.redirect(uri);
        }
        session.put(AuthSessionKeys.CLIENTE_ID, cliente.get().getId());
        return HttpResponse.redirect(URI.create("/clientes"));
    }

    @Post("/logout")
    public MutableHttpResponse<?> logout(@Nullable Session session) {
        if (session != null) {
            sessionStore.deleteSession(session.getId()).join();
        }
        return HttpResponse.redirect(URI.create("/login"));
    }
}
