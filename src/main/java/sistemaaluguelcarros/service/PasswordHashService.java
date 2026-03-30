package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Singleton
public class PasswordHashService {

    public String hash(String senhaPlana) {
        return BCrypt.hashpw(senhaPlana, BCrypt.gensalt());
    }

    public boolean matches(String senhaPlana, String senhaHash) {
        if (senhaPlana == null || senhaHash == null || senhaHash.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(senhaPlana, senhaHash);
    }
}
