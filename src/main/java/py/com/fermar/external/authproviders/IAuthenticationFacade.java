package py.com.fermar.external.authproviders;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    String getUsuarioId();
    HttpSession getSession();
    void setTrustedToken(String token);
    String getTrustedToken();
}
