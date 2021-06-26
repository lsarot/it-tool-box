package com.example.springsecdemo.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
	//### protected void configure(HttpSecurity http) Vs protected void configure(WebSecurity web)
	// https://stackoverflow.com/questions/56388865/spring-security-configuration-httpsecurity-vs-websecurity/56389047
		
		
	@Bean
	public PasswordEncoder passwordEncoder() {
		//PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
	
	/* COMO USAREMOS VALIDACIÓN POR JWT USANDO OAUTH2 (Y NO BASIC OR DIGEST OR FORMLOGIN...), QUITAMOS ESTO QUE ES PARA ACCEDER CON MIS CREDENCIALES A ESTE SERVIDOR
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = passwordEncoder();      
        auth
		  	.inMemoryAuthentication()
		  	.passwordEncoder(encoder)
		  	.withUser("admin").password(encoder.encode("admin")).roles("USER", "ADMIN")
		  	.and().withUser("leo").password(encoder.encode("pass")).roles("USER")
		  	.and().withUser("user").password(encoder.encode("password")).roles("USER");
    }
	*/
	
	
	/**
	 * SIENDO ESTE EL RS, expone datos del usuario, aquí seteamos los scopes necesarios que debe tener el token expedido por el AS
	 * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	//VER DETALLES DE MÉTODOS EN PROYECTO SpringWebDemo
        http
        	.cors()
            .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/user/info", "/api/emps/**")
                  .hasAuthority("SCOPE_read")
                .antMatchers(HttpMethod.POST, "/api/emps")
                  .hasAuthority("SCOPE_write")
                .anyRequest()
                  .authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt();
    }
}
