package com.example.springjwtauthentication.configuration.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.springjwtauthentication.configuration.security.httpfilter.JwtAuthenticationFilter;

@Configuration
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {

	//Se usaba con la versión antigua. Cuando UserService implementaba UserDetailsService interface.
    //@Resource(name = "userService")
    //private UserDetailsService userDetailsService; // to fetch user credentials from database.
    /*@Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }*/
    
	//Se registró directamente en la configuración de abajo
    //@Autowired(required = true) private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired private DataSource dataSource;
	
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }
    
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    
    
    /**
     * ESTOS USERS ESTARÁN EN LA TABLA USERS (del Spring sec schema) (NO USER, la nuestra)
     * TENDRÍA QUE BUSCAR LUEGO EN ESA TABLA EL REGISTRO CORRESPONDIENTE,
     * NO ME SERVIRÁ EL REPOSITORY DE User
     * 
     * PERO LO IDEAL ES TRABAJAR CON NUESTRO MODELO Y ADAPTAR LAS QUERIES COMO YA SE VIÓ!
     * */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = encoder(); // al llamar a encoder(), se pide un new instance del objeto, y se registra otro en el contenedor.. debemos separar en otra clase config esta dependencia para que aquí inyectemos la que tenga el contenedor.
		auth
			//.inMemoryAuthentication()
			.jdbcAuthentication().dataSource(dataSource).withDefaultSchema()
			.passwordEncoder(encoder)
			.withUser("admin").password(encoder.encode("admin")).roles("USER", "ADMIN")
			.and().withUser("uname").password(encoder.encode("psw")).roles("USER", "ADMIN");
    }
    
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	//SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL); // Changes the preferred strategy. Do NOT call this method more than once for a given JVM, as it will re-initialize the strategy and adversely affect any existing threads using the old strategy.
    	
        http
        		.cors()//de jwt.io -> If the token is sent in the Authorization header, Cross-Origin Resource Sharing (CORS) won't be an issue as it doesn't use cookies.
        		.and().csrf().disable()
        		.authorizeRequests()
                .antMatchers(
                		"/token/*",
                		"/signup"
                		).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling(e -> e
                		.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                //.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                //.and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

}
