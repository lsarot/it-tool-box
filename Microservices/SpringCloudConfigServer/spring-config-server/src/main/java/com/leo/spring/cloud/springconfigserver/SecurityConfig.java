package com.leo.spring.cloud.springconfigserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * By default Spring Security enables CSRF protection for all the requests sent to our application.
 * Therefore, to be able to use the /encrypt and /decrypt endpoints, let's disable the CSRF for them
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
          .ignoringAntMatchers("/encrypt/**")
          .ignoringAntMatchers("/decrypt/**");

        super.configure(http);
    }
}
