package py.com.fermar.external.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import py.com.fermar.commons.utils.StringsUtils;
import py.com.fermar.external.service.AuthenticationManagerService;


@Service
public class AuthenticationManagerServiceImpl implements AuthenticationManagerService {

    @Autowired
    private String serviceApiKey;
    
    @Override
    public AuthenticationManager getAuthenticationManager() {
        return authentication -> {
        	try {
	            String principal = (String) authentication.getCredentials();
	            String hash = StringsUtils.sha256(principal);
	            if (serviceApiKey == null || 
	            		!(serviceApiKey.equals(hash) ||
	            				serviceApiKey.equals(principal))) {
	                throw new BadCredentialsException("No se encontró el API key o su valor es incorrecto");
	            }
	            authentication.setAuthenticated(true);
	            return authentication;
        	}catch(Exception e) {
                throw new BadCredentialsException("No se encontró la cabecera de seguridad");        		
        	}
        };
    }
}
