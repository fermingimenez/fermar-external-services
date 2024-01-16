package py.com.fermar.external.config;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private String apiKeyRequestHeader;
    private String userRequestHeader;
    
    private static final String DEFAULT_USER = "defaultUser";

    public APIKeyAuthFilter(String apiKeyRequestHeader, String userRequestHeader) {
        this.apiKeyRequestHeader = apiKeyRequestHeader;
        this.userRequestHeader = userRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    	if(DEFAULT_USER.equals(userRequestHeader)) {
    		return DEFAULT_USER;
    	}
    	String userRequest = request.getHeader(userRequestHeader);
    	
        return (userRequest == null ? DEFAULT_USER : userRequest);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(apiKeyRequestHeader);
    }

}