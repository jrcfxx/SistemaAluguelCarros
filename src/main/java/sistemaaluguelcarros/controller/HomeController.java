package sistemaaluguelcarros.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import sistemaaluguelcarros.auth.AuthSessionKeys;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class HomeController {

    @Get("/")
    public ModelAndView<Map<String, Object>> index(@Nullable Session session) {
        Map<String, Object> model = new LinkedHashMap<>();
        boolean autenticado = session != null
                && session.get(AuthSessionKeys.CLIENTE_ID, Long.class).isPresent();
        model.put("autenticado", autenticado);
        return new ModelAndView<>("home", model);
    }
}
