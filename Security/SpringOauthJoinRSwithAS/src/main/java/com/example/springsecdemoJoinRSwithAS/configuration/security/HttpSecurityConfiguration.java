package com.example.springsecdemoJoinRSwithAS.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(1)
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	/**
	 * ESTO ES DEL AUTHORIZATION SERVER
	 * 
	 * COMO USAREMOS VALIDACIÓN POR JWT USANDO OAUTH2 (Y NO BASIC OR DIGEST OR FORMLOGIN...),
	 * QUITAMOS ESTO QUE ES PARA ACCEDER CON MIS CREDENCIALES A ESTE SERVIDOR
	 * 
	 * seteamos cuentas de usuario en memoria esta vez
	 * */
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
    
    
	/**
	 * ESTO APLICA PARA RS O AS POR IGUAL
	 * pero /login y /oauth/authorize son del AS
	 * se usará un formLogin para pedir credenciales de cualquier petición al dominio. Ya sea del AS o RS
	 * 			no es que se abrirá una pantalla diferente como la de acceso a KeyCloak
	 * */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				//.cors().and().csrf().disable()
				.requestMatchers()
				.antMatchers(
						"/login", // /login pq necesitarás hacer login frente al AS
						"/oauth/authorize" // /authorize pq es el primer contacto con el AS.. /token no pq requiere estar autorizado
						)
				.and()
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.formLogin().permitAll();
	}
 
}
