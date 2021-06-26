package com.example.springsecdemoOauthClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * VER DESCRIPCIÓN EN PROYECTO SpringSecurityDemo
 * 
 * LO USAREMOS PARA:
 * - Autenticar/Autorizar con third-party providers such as Github or Google
 * 
 * TUTORIAL: https://spring.io/guides/tutorials/spring-boot-oauth2/
 * 
 * 
 * Para registrar Google como proveedor:
 * https://developers.google.com/identity/protocols/oauth2/openid-connect
 * 
 * PARA VER MÁS PODEMOS SEGUIR EL TUTORIAL, no continuamos pq es orientado todo a web, no a Android.
 * 
 * 
 * Notes:
 * If you stay logged in to GitHub, you won’t have to re-authenticate with this local app, even if you open it in a fresh browser with no cookies and no cached data. (That’s what Single Sign-On means.) (ESTO NO FUE ASÍ, Y ESTO NO ES SSO, lo coloco pq esto fue en la doc de Spring oficial)
 * 
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
