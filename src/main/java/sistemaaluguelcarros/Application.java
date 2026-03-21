package sistemaaluguelcarros;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        loadDotenvIntoSystemProperties();
        Micronaut.run(Application.class, args);
    }

    private static void loadDotenvIntoSystemProperties() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        for (DotenvEntry entry : dotenv.entries()) {
            String key = entry.getKey();
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, entry.getValue());
            }
        }
    }
}
