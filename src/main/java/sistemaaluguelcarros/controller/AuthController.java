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
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.service.AgenteSessionService;
import sistemaaluguelcarros.service.AuthService;
import sistemaaluguelcarros.service.SessionAuthService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private static final String MSG_LOGIN_INVALIDO = "CPF ou senha inválidos. Verifique os dados e tente novamente.";

    private final AuthService authService;
    private final SessionAuthService sessionAuthService;
    private final AgenteSessionService agenteSessionService;

    public AuthController(
            AuthService authService,
            SessionAuthService sessionAuthService,
            AgenteSessionService agenteSessionService
    ) {
        this.authService = authService;
        this.sessionAuthService = sessionAuthService;
        this.agenteSessionService = agenteSessionService;
    }

    @Get("/login")
    public Object loginForm(
            @Nullable Session session,
            @Nullable @QueryValue String erro,
            @Nullable @QueryValue String mensagem
    ) {
        if (sessionAuthService.isAutenticado(session)) {
            return HttpResponse.redirect(URI.create("/clientes"));
        }
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
            return HttpResponse.seeOther(uri);
        }
        agenteSessionService.limparSessao(session);
        sessionAuthService.autenticar(session, cliente.get());
        return HttpResponse.seeOther(URI.create("/clientes"));
    }

    @Post(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> logout(@Nullable Session session) {
        sessionAuthService.limparSessao(session);
        agenteSessionService.limparSessao(session);
        return HttpResponse.seeOther(URI.create("/login"));
    }
}
