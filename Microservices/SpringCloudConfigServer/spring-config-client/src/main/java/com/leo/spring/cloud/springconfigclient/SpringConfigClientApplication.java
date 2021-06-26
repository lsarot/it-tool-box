package com.leo.spring.cloud.springconfigclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope // By default, the configuration values are read on the client’s startup and not again. You can force a bean to refresh its configuration (that is, to pull updated values from the Config Server) by triggering a refresh event, with a POST req:
	// curl localhost:8080/actuator/refresh -d {} -H "Content-Type: application/json"
@RestController
@SpringBootApplication
public class SpringConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringConfigClientApplication.class, args);
	}
	
	
	@Value("${user.role}") private String role;

	@Value("${user.password}") private String password;
	
	@GetMapping(value = "/whoami/{username}",  produces = MediaType.TEXT_PLAIN_VALUE)
    public String whoami(@PathVariable("username") String username) {
    	
        return String.format("Hello! You're %s " +
        	"and you'll become a(n) %s, " +
        	"but only if your password is '%s'!\n", 
        	username, role, password);
    }

}
