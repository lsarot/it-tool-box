package com.example.springwebdemo.configuration.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.example.springwebdemo.configuration.security.httpfilter.EndpointRequestFilters;

/** SPRING SECURITY GUIDE
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5/
 */

@Configuration
//@EnableWebSecurity //crucial if we disable the default security configuration.. (SE QUITA SI ESTAMOS SOBREESCRIBIENDO, SE DEJA SI ES FROM SCRATCH).. is only optional if we're just overriding the default behavior using a WebSecurityConfigurerAdapter..
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
	
	/**
	 * TO STORE CREDENTIALS
	 * */
	@Qualifier("credentials-store-default")
	@Autowired private DataSource credentialsDefaultSpringSchema;
	
	@Qualifier("credentials-store-own")
	@Autowired private DataSource credentialsOwnSchema;
	
	//// https://docs.spring.io/spring-security/site/docs/current/reference/html5/
	
	/** The mappings current are:
		bcrypt - BCryptPasswordEncoder (Also used for encoding)
		ldap - org.springframework.security.crypto.password.LdapShaPasswordEncoder
		MD4 - org.springframework.security.crypto.password.Md4PasswordEncoder
		MD5 - new MessageDigestPasswordEncoder("MD5")
		noop - org.springframework.security.crypto.password.NoOpPasswordEncoder
		pbkdf2 - Pbkdf2PasswordEncoder
		scrypt - SCryptPasswordEncoder
		SHA-1 - new MessageDigestPasswordEncoder("SHA-1")
		SHA-256 - new MessageDigestPasswordEncoder("SHA-256")
		sha256 - org.springframework.security.crypto.password.StandardPasswordEncoder
		argon2 - Argon2PasswordEncoder
		...view PasswordEncoder interface implementations
	 **/
	/* VER EL TEST DONDE SE PRUEBAN LOS DIFERENTES ALGORITMOS
	 * Los de cifrado 1-way deben ser tuneados para que demore aprox 1s descifrar la psw.
	 * bcrypt is deliberately slow
	 * */
	@Bean
	public PasswordEncoder passwordEncoder() {
		//return PasswordEncoderFactories.createDelegatingPasswordEncoder(); ***** es más flexible (VER AL FONDO NOTA-2)
		return new BCryptPasswordEncoder();// funciona incluso si le envío Base64 encoded desde el cliente. Esto es sólo cómo va a guardar las psw en el servidor
	}
	
	
 	/*
	Notice that we need to use the PasswordEncoder to set the passwords when using Spring Boot 2. In Spring Security 4 (or Spring Boot 1), it was possible to store passwords in plain text ( we do .password(encoder.encode("psw")), not .password("psw") ).
	Now, with this configuration we're storing our in-memory password using BCrypt in the following format: {bcrypt}$2a$10$MF7hYnWLeLT66gNccBgxaONZHbrSMjlUofkp50sSpBw2PJjUqU.zS
	Although we can define our own set of password encoders, it's recommended to stick with the default encoders provided in PasswordEncoderFactories.
 	*/
 	/** Migrating existing passwords: (3 solutions)

		    . Updating plain text stored passwords with their value encoded: String encoded = new BCryptPasswordEncoder().encode(plainTextPassword);
		    . Prefixing hashed stored passwords with their known encoder identifier like: {bcrypt}$2a$..... | {sha256}97cde38..... (agregamos {bcrypt} al inicio por ejemplo)
		    . Requesting users to update their passwords when the encoding-mechanism for stored passwords is unknown
 	*/
	/* IN-MEMORY AUTH ES PARA APPS QUE TIENEN POCOS USUARIOS (MUY POCOS) Y PRUEBAS
	 * 	JDBC AUTH RECOMIENDA USAR OTRAS HERRAMIENTAS Y AGREGAR USUARIOS POR FUERA DE ESTA CONFIGURACIÓN. (la forma .withUser("admin").passw.. es para pruebas básicamente)
	 * */
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//inMemory(auth);
		inDataBase_UsingSpringDefaultSchema(auth);
		//inDataBase_UsingMyOwnSchema(auth);
    }
	
	
	/**
	 * la guía arriba expuesta de Spring, explica otra forma de declararlo, igualmente no aporta ningún extra (usando UserDetails).
	 * 		creo que la diferencia es que esa guía explica para hacerlo en Spring Standard y esto es Spring Boot
	 * */
	private void inMemory(AuthenticationManagerBuilder auth) throws Exception {
		System.out.println("** In memory credentials storage **");
		PasswordEncoder encoder = passwordEncoder();
		auth
			.inMemoryAuthentication()
			.passwordEncoder(encoder)
			.withUser("admin").password(encoder.encode("admin")).roles("USER", "ADMIN")//.authorities("ROLE_ADMIN","ROLE_USER")
			.and().withUser("leo").password(encoder.encode("pass")).roles("USER")
			.and().withUser("user").password(encoder.encode("password")).roles("USER")
			.and().withUser("user2").password(encoder.encode("pass")).roles("USER").disabled(true)
			.and().withUser("user3").password(encoder.encode("pass")).roles("USER").credentialsExpired(true)
			.and().withUser("user4").password(encoder.encode("pass")).roles("USER").accountExpired(true)
			.and().withUser("user5").password(encoder.encode("pass")).roles("USER")//.accountLocked(true);
			.and().withUser("anonimo").password(encoder.encode("password")).roles("ANONYMOUS").authorities("ROLE_ANON");
	}
 
	/** Spring Boot permite gestionar los usuarios en un ddbb schema ya preparado.
	 * Si queremos hacer uso de este, usamos .withDefaultSchema() y poblamos la tabla desde aquí, desde la H2 console, o desde nuestro rdbms favorito.
	 * 	https://docs.spring.io/spring-security/site/docs/current/reference/html5/#user-schema
	 * 
	 * Si en la url de la bbdd (jdbcUrl) le decimos a H2 que use un script .sql, se agregarán las tablas users y authorities a dicha bbdd
	 * Debo borrar la bbdd al ejecutar pq dice que las tablas Users y Authorities ya existen
	 * 			Si las borro desde el initial script mencionado, tampoco se volverán a crear.
	 * 			Seguro se puede configurar esto, pero no profundizaremos en esta sino en utilizar/adaptar nuestro propio esquema de bbdd.
	 * 
	 * Si cambio la versión del Driver H2, fallará en el arranque, es un tema de seguridad parece.
	 * 
	 * usemos el Rest endpoint creado en AccountController.java ( /principal ) para probar la entidad que accedió al sistema, o simplemente accedamos por consola ( /h2-console ) o a cualquier página que pida credencial
	 * */
	private void inDataBase_UsingSpringDefaultSchema(AuthenticationManagerBuilder auth) throws Exception {
		System.out.println("** In DDBB credentials storage - with SpringBoot Default schema **");
		PasswordEncoder encoder = passwordEncoder();   
		auth
			.jdbcAuthentication() //	--> When using with a persistent data store, it is best to add users external of configuration using something like Flyway or Liquibase to create the schema and adding users to ensure these steps are only done once and that the optimal SQL is used.
			.passwordEncoder(encoder)
			.dataSource(credentialsDefaultSpringSchema)
					//ya este DataSource inició la bbdd con un script que trae Spring.
					//usar withDefaultSchema usa un DDL para crear la bbdd, pero ya nuestro datasource configuró un schema que se ajusta
			.withDefaultSchema() //adds a database script that will populate the default schema, allowing users and authorities to be stored... the DDL script provided with the withDefaultSchema directive uses a dialect not suitable to all DDBB.
			.withUser(User.withUsername("admin").password(encoder.encode("pass")).roles("USER", "ADMIN")); //we're creating an entry in the database with a default user programmatically.
	}
	
	/** Adaptamos nuestro propio DDBB schema al modelo de credenciales de Spring
	 * 
	 * There is one approach to achieve this, by implementing the UserDetailsService interface ourselves.
	 * But is quite tedious (Spring impl of this interface is JdbcDaoImpl), so we are going to use a newer approach.
	 * 
	 * En la config abajo, se seteó que se buscara al usuario con x query, y se tomará email de nuestro schema como username.
	 * */
	private void inDataBase_UsingMyOwnSchema(AuthenticationManagerBuilder auth) throws Exception {
		System.out.println("** In DDBB credentials storage - with my own DDBB schema **");
		PasswordEncoder encoder = passwordEncoder();   
	    auth
	    	.jdbcAuthentication()
	    	.passwordEncoder(encoder)
	    	//since our schema may differ from the default used by Spring, we can customize the queries used to retrieve user details in the authentication process.
	    	.usersByUsernameQuery("select email,password,enabled from accounts where email = ?")
	    	.authoritiesByUsernameQuery("select email,authority from authorities where email = ?")
	    	.dataSource(credentialsOwnSchema);
	}
	
	//-----------------------------------------------------
	
	/**
	 * Aquí da igual si es inMemory or inDataBase.. son sólo configuraciones distintas que preparamos para los endpoints
	 * */
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		//inMemory(http);
        inDataBase(http);
    }
	
	private void inMemory(HttpSecurity http) throws Exception {
		http
    	.authorizeRequests()
        .antMatchers("/private/**")
        		.hasRole("USER")
        .antMatchers("/public/**")
            	.permitAll()
        .anyRequest()
            	.authenticated()
        .and()
        .httpBasic()
        .realmName("Custom Realm") //will say: Server says 'Custom Realm' when browser prompt for credentials
        //.formLogin();
    	.and()
        .sessionManagement()
		        //.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //ninguna evita que se envíe header JSESSIONID.. parece que es el Servlet. se dejó una nota en las guías .txt sobre el tema security //quizás se genera el sessionID pero no se toma en cuenta!
		        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
		.and()
		//esta forma de instanciar un filtro en la cadena de filtros no es la ideal. Se debe crear uno aparte e insertar.
		/*.addFilter(new Filter() {
			@Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				//en este, que no indica un orden,
				//lo que coloquemos antes de chain.doFilter se ejecutará al inicio de la filter chain
				//lo que coloquemos luego de chain.doFilter se jecutará al final de t0do el request, incluso de haber hecho el response (de ejecutarse el llamado al endpoint y la lógica que este contenía!)
			}})*/
		.addFilterBefore(new Filter() {
			@Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				System.out.println("before filter");
				Filter delegate = EndpointRequestFilters.myCustomTokenFilter();
				delegate.doFilter(request, response, chain);
				System.out.println("after filter");
				
				chain.doFilter(request, response); // invoke the rest of the application
				
				System.out.println("after chain.doFilter()");	// do something after the rest of the application (ESTO SE EJECUTARÁ LUEGO DE DEVOLVER LA RSPTA AL CLIENTE INCLUSO, AL FINAL DE T0DO)
			}}, UsernamePasswordAuthenticationFilter.class);
	}
	
	private void inDataBase(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
				.antMatchers("/h2-console/**")
				.permitAll()
		.anyRequest()
				.authenticated()
		.and()
		.formLogin();
     
	    http.csrf()
	    		.ignoringAntMatchers("/h2-console/**");
	
	    http.headers()
				.frameOptions()
				.sameOrigin();
	}
	
	
	
	//------------------------------------------------------------------- GUIDE FOR HTTP SECURITY CONFIGURATION
	//remember it is already configured, this will overwrite default configurations, 
	//but not affecting where we dont touch 
	
	
	
    /** TODAS LAS CONFIGURACIONES YA TIENEN ALGO SETEADO CON LA AUTO-CONFIGURATION DE SPRING BOOT
     * SI QUEREMOS HACER UNA DESDE CERO, USAR @EnableWebSecurity, y desactivar auto-config (con exclude en @SpringBootApplication)
     * 			SI DESHABILITAMOS AUTO-CONFIG, USAMOS @EnableWebSecurity EN UN WebSecurityConfigurerAdapter
     * SI QUEREMOS SOBREESCRIBIR, NO USAR @EnableWebSecurity y dejar activo auto-config
     * */
    //@Override
    protected void configureGuide(HttpSecurity http) throws Exception {
    	System.out.println("* configuring HttpSecurity *");
    	
        http
        	//.cors() //to allow Access-Control headers on the requests. This is specially important when we are dealing with an Angular client, and our requests are going to come from another origin URL.
        	//.and()
        	//.requestMatchers() //ver doc, facilita especificar las url a las que se le aplica este http security
	        .authorizeRequests()
	        		/*.antMatchers("/**")
	        				.hasRole("USER").and().formLogin()
	        							.permitAll().and()
	        									.rememberMe()*/
			        /*.antMatchers("/private/**")
			    			.authenticated()
			    	.antMatchers("/public/**")
			    			.authenticated()
			    			//.permitAll()*/
			    	//.mvcMatchers("/url-enabled-for-anon")
			    			//.anonymous()
	        		//.antMatchers(HttpMethod.POST, "/api/foos")
	                  		//.hasAuthority("SCOPE_write")//SCOPE_read
	                .anyRequest()
	                  		.authenticated()
    		.and()
    		.anonymous().authorities("ROLE_ANON")
	        //.and()
	        //.logout().deleteCookies("remove").invalidateHttpSession(true) //No me funcionó ni con formLogin
					//.logoutUrl("/logout/index.html").logoutSuccessUrl("/logout-success.html")
    		.and()
	        //.httpBasic() //DEBEMOS USAR BASIC CUANDO NO ES PÁGINA WEB, YA QUE EN FORM LOGIN ENVÍA POR BODY Y NO POR HEADER LAS CREDENCIALES (si en un request enviamos credenciales por adelantado en los headers, no las considerará!) (se podría arreglar, pero mejor no si no tiene GUI web nuestra app)
    				//The main reason that form-based authentication is not ideal for a RESTful Service is that Spring Security will make use of Sessions – this is of course state on the server, so the statelessness constraints in REST is practically ignored.
    				//.realmName("custom realm")
    		.formLogin() //para usar /logout debemos usar formLogin parece (esto es para webs)
    		//.and()
    		//.oauth2ResourceServer() //specifies that this is a resource server, with jwt() formatted tokens.
            		//.jwt()
    		//.and().requiresChannel().anyRequest().requiresSecure() //si colocamos 8080 nos redirige a 8443
    		/*
    		 * orden de los filtros: https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-security-filters
    		 * 
    		.and()
    		.addFilterBefore(new Filter() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				// do something before the rest of the application
				System.out.println("before filter");
				Filter delegate = aCustomFilterBean();
				delegate.doFilter(request, response, chain);// Lazily get Filter that was registered as a Spring Bean
				System.out.println("after filter");
				chain.doFilter(request, response); // invoke the rest of the application	
				// do something after the rest of the application
			}}, UsernamePasswordAuthenticationFilter.class)
			*/
    		/*
			.and()
            .addFilterAfter(
            		new DigestAuthenticationFilter() {}  ***(ver NOTA-1)
            		, BasicAuthenticationFilter.class)
    		*/
	        .and()
	        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //enviará un session dif en cada petición
	        		//.invalidSessionStrategy(new InvalidSessionStrategy() {})
	        		//.sessionAuthenticationStrategy(new sessionAuthenticationStrategy() {})
	        .sessionManagement()
			        //.disable()
			        .maximumSessions(1)
			        		.expiredUrl("/login?expired");//creo que sólo redirige si es basic auth, no form login
        					//.maxSessionsPreventsLogin(true);
        
        
        	//-----------------------------------
        
        	// *** Algunos métodos se llaman igual pero no devuelven lo mismo. ie. anyRequest() de authorizeRequests() y de requiresChannel()
        
        	//.authorizeRequests()	que pida credenciales
        			//.antMatchers("/**")
        			//.anyRequest()	cualquier url que pidan
        					//.hasRole("USER")
        					//.hasAnyRole()
        					//.hasAuthority("ROLE_USER")
        					//.hasAnyAuthority()
        					//.authenticated()	permitir si se autentica exitosamente
        					//.fullyAuthenticated() //se autentica aunque tenga una sesión activa
        	//.addFilter(filter)
        	//.addFilterAt(filter, atFilter)
        	//.addFilterBefore(filter, beforeFilter)
        	//.addFilterAfter(filter, afterFilter)
	        //.httpBasic()	formulario del navegador (psw base64 encoded)
	        //.formLogin()	con formulario (redirige a /login basicamente y funciona /logout)
	        //.anonymous()	usuario que no envíe clave debe cumplir tal requisito ie. anonymous().authorities("ROLE_ANON")
	        //.exceptionHandling().accessDeniedHandler(accessDeniedHandler)
	        //.requestCache()	devuelve a la pag que pidió luego de hacer el login
	        //.headers()
	        //.logout()	va a /logout
	        //.rememberMe()		Upon authenticating if the HTTP parameter named "remember-me" exists, then the user will be remembered even after their javax.servlet.http.HttpSession expires. (With a token).
	        //.sessionManagement()		cantidad de sesiones del usuario en simultáneo
	        //.requiresChannel(requiresChannelCustomizer)	(https) .and().requiresChannel().anyRequest().requiresSecure()
	        //.mvcMatcher(mvcPattern)
		    //.oauth2Login()
		    //.oauth2Client()
	        //.openidLogin()
	        //.saml2Login()
	        //.x509()
    }
    
}

/** NOTA-1
 * Anonymous Request
					With both basic and digest filters in the security chain, the way an anonymous request – a request containing no authentication credentials (Authorization HTTP header) – is processed by Spring Security is – the two authentication filters will find no credentials and will continue execution of the filter chain. Then, seeing how the request wasn't authenticated, an AccessDeniedException is thrown and caught in the ExceptionTranslationFilter, which commences the digest entry point, prompting the client for credentials.
					The responsibilities of both the basic and digest filters are very narrow – they will continue to execute the security filter chain if they are unable to identify the type of authentication credentials in the request. It is because of this that Spring Security can have the flexibility to be configured with support for multiple authentication protocols on the same URI.
					When a request is made containing the correct authentication credentials – either basic or digest – that protocol will be rightly used. However, for an anonymous request, the client will get prompted only for digest authentication credentials. This is because the digest entry point is configured as the main and single entry point of the Spring Security chain; as such digest authentication can be considered the default.
 * */

/** NOTA-2
 * DelegatingPasswordEncoder
Prior to Spring Security 5.0 the default PasswordEncoder was NoOpPasswordEncoder which required plain text passwords.
Based upon the Password History section you might expect that the default PasswordEncoder is now something like BCryptPasswordEncoder. However, this ignores three real world problems:
    -There are many applications using old password encodings that cannot easily migrate
    -The best practice for password storage will change again.
    -As a framework Spring Security cannot make breaking changes frequently
Instead Spring Security introduces DelegatingPasswordEncoder which solves all of the problems by:
    Ensuring that passwords are encoded using the current password storage recommendations
    Allowing for validating passwords in modern and legacy formats
    Allowing for upgrading the encoding in the future
    
Create own:
    
    Ver tests donde se probó encriptadores (en este mismo proyecto) **
 * */



