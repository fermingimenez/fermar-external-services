package py.com.fermar.external.service;

import org.springframework.security.authentication.AuthenticationManager;

public interface AuthenticationManagerService {
    AuthenticationManager getAuthenticationManager();
}
