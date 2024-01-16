package py.com.fermar.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import py.com.fermar.external.config.APIKeyAuthFilter;
import py.com.fermar.external.service.AuthenticationManagerService;

@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String API_KEY_REQUEST_HEADER = "service-api-key";    
    private static final String USER_REQUEST_HEADER = "consumer-username";

    @Autowired
    private AuthenticationManagerService authenticationManagerService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(API_KEY_REQUEST_HEADER, USER_REQUEST_HEADER);
        filter.setAuthenticationManager(authenticationManagerService.getAuthenticationManager());
        httpSecurity.
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().addFilter(filter).authorizeRequests().antMatchers("/fermar-connect-eventos/eventos/**").authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                                   "/configuration/ui",
                                   "/swagger-resources/**",
                                   "/configuration/security",
                                   "/swagger-ui.html",
                                   "/webjars/**");
    }

}
