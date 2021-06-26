package com.example.springsecdemoJoinRSwithAS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * VER DESCRIPCIÓN EN PROYECTO 'demo-spring-sec'
 * */

@SpringBootApplication//(exclude = {
			// SecurityAutoConfiguration.class,
			// OAuth2AutoConfiguration.class
			//})
@EnableResourceServer//El RS trabaja con el AS, este provee data del propietario de la cuenta para ser accedida por la app cliente.
		//proveedores como Google, Facebook, etc, hacen rol de AS y RS
public class JoinRSwithASApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JoinRSwithASApplication.class, args);
	}
}
