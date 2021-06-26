package com.example.springsecdemoOauthClient.configuration.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableOAuth2Sso //ESENCIA DEL CLIENTE OAUTH SPRING
		//enhances security by adding an authentication filter and an authentication entry point. If the user only has @EnableOAuth2Sso but not on a WebSecurityConfigurerAdapter then one is added with all paths secured.
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
	
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
	
	@Override
    public void configure(HttpSecurity http) throws Exception {
        http
	        .antMatcher("/**")
	        .authorizeRequests()
	        .antMatchers("/", "/login**")
	        .permitAll()
	        .anyRequest()
	        .authenticated();
	        //.and()
	        //.formLogin(); //está por defecto!, pero al usar @EnableOAuth2Sso activa el login con el AS, redirigiendo a su página de inicio de autenticación.
    }
	
	/* FLUJO DEL PROCESO DE AUTENTICACIÓN:
	 * Siendo el context-path: /ui
	 * Usuario accede a localhost:8082/ui/index.html, está permitido. Y /login también
	 * clic en un link que lleva a /securedPage.html, necesita autenticarse
	 * 		entonces se redirige a :8082/../login
	 * 		como esta app usa Oauth, se redirige la petición al AS
	 * 				localhost:8081/auth/oauth/authorize?client_id=SampleClientId&redirect_uri=http://localhost:8082/ui/login&response_type=code&state=L7qVF1
	 * 		como no está autenticado con el AS, se le pide credenciales, redirigiendo a
	 * 				localhost:8081/auth/login (es del AS)
	 * 		se logea y se redirige al RS con el code
	 * 				localhost:8082/ui/login?code=Yh8yDr&state=4Oaef6
	 * 		con el code, RS permite entrar a la url solicitada
	 * 				localhost:8082/ui/securedPage.html
	 * */
    
}
