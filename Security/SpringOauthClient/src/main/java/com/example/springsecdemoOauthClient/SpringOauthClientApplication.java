package com.example.springsecdemoOauthClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * VER DESCRIPCIÓN EN PROYECTO SpringSecurityDemo
 * 
 * LO USAREMOS PARA:
 * - Autenticar/Autorizar con nuestro propio AS/RS (SpringOauthJoinRSwithAS)
 * - Autenticar/Autorizar con third-party providers such as Github or Google (EN EL CLIENTE 2)
 * 
 * */

@SpringBootApplication//(exclude = {
			// SecurityAutoConfiguration.class,
			//})
public class SpringOauthClientApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringOauthClientApplication.class, args);
	}
}
