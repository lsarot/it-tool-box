package com.example.springsecdemoOauthClient.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
//@EnableOAuth2Sso //ESENCIA DEL CLIENTE OAUTH SPRING (nótese que no hizo falta en este tutorial, pero también usamos spring-boot-starter-oauth2-client en el pom)
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
	
	//-----------------------
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll() // We switch off the security on the home page, and others (/webjars/** since you’ll want your JavaScript to run for all visitors, authenticated or not)
                .anyRequest().authenticated()
            )
            //si una ruta está protegida, por defecto redirige a hacer el login con el proveedor registrado
            //si activamos esto, es que queremos customizar las excepciones de autenticación
            		//pero se desactiva la vista /login que mostraba los varios proveedores que teníamos registrados!(habrá otra manera de mostrarla)
            		//defaults to new Http403ForbiddenEntryPoint()
            		//en este caso devolvemos 401 Unauthorized
            		//new AuthenticationEntryPoint() {} si queremos crear el nuestro
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            //will do the right thing for us (clear the session and invalidate the cookie).
            .logout(l -> l
            		.logoutSuccessUrl("/").permitAll()
            )
            //The /logout endpoint requires us to POST to it, and to protect the user from Cross Site Request Forgery (CSRF, pronounced "sea surf"), it requires a token to be included in the request. The value of the token is linked to the current session, which is what provides the protection, so we need a way to get that data into our JavaScript app.
            //Many JavaScript frameworks have built in support for CSRF (e.g. in Angular they call it XSRF), but it is often implemented in a slightly different way than the out-of-the box behaviour of Spring Security. For instance, in Angular, the front end would like the server to send it a cookie called "XSRF-TOKEN" and if it sees that, it will send the value back as a header named "X-XSRF-TOKEN". We can implement the same behaviour with our simple jQuery client, and then the server-side changes will work with other front end implementations with no or very few changes. To teach Spring Security about this we need to add a filter that creates the cookie.
            //y la etiqueta @CrossOrigin y el .cors de aquí, qué dif hay?
            .csrf(c -> c
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .oauth2Login();//nótese su uso, diferente al otro SpringOauthClient (que se autentica con un AS/RS mío)
    }
    
}
