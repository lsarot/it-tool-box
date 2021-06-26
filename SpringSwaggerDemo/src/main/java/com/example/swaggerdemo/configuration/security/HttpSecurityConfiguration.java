package com.example.swaggerdemo.configuration.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableOAuth2Sso //ESENCIA DEL CLIENTE OAUTH SPRING
		//enhances security by adding an authentication filter and an authentication entry point.
		//If the user only has @EnableOAuth2Sso but not on a WebSecurityConfigurerAdapter then one is added with all paths secured.
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Bean public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	PasswordEncoder encoder = passwordEncoder();   
        auth
        	.inMemoryAuthentication()
        	.passwordEncoder(encoder)
        	.withUser("admin").password(encoder.encode("admin")).roles("USER", "ADMIN")
        	.and().withUser("leo").password(encoder.encode("pass")).roles("USER")
            .and().withUser("john@test.com").password(encoder.encode("123")).roles("USER")
            .and().withUser("user").password(encoder.encode("password")).roles("USER");
    }
	
	
	@Override
    public void configure(HttpSecurity http) throws Exception {
        http
        	.cors()
        	.and().csrf().disable()
        	//.csrf(c -> c
              //      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            //)
	        .antMatcher("/**")
	        .authorizeRequests(a -> a
	        		.antMatchers(
	        				"/", "/login**"
	        				, "/error", "/webjars/**"
	        				//, "/swagger-ui.html**"       //si lo coloco no carga la página
	        				//, "/swagger-ui/index.html**"
	        				).permitAll()
	        		.anyRequest()
	    	        .authenticated()
	    	        //.permitAll()
	        )
	        //.and()
	        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        //.and()
	        //.formLogin()   //está por defecto!, pero al usar @EnableOAuth2Sso activa el login con el AS, redirigiendo a su página de inicio de autenticación.
	        	//.permitAll()
	        ;
    }
	
}
