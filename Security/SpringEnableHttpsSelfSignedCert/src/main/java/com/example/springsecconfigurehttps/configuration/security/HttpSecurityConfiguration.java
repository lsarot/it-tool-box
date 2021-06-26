package com.example.springsecconfigurehttps.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	//.antMatcher("/**").requiresChannel().anyRequest().requiresSecure().and()
        	.authorizeRequests()
        	.antMatchers("/**")
        	.permitAll();
        
        //en algún ejercicio se probó que con esto se recargaba la página con https.
        //pero ahora no sirve!
        //http
        	//.requiresChannel().anyRequest().requiresSecure();
    }

}
