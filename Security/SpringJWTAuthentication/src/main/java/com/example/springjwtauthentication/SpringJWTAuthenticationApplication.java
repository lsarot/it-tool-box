package com.example.springjwtauthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * VER DESCRIPCIÓN EN PROYECTO DemoSpringSec
 * 
 * LO USAREMOS PARA:
 * -Autenticar al usuario nosotros mismos con un JWT (SIN OAuth2)
 * 
 * 
 * JSON Web Token (JWT) is a compact, URL-safe means of representing claims to be transferred between two parties. The claims in a JWT are encoded as a JavaScript Object Notation (JSON) object that is used as the payload of a JSON Web Signature (JWS) structure or as the plaintext of a JSON Web Encryption (JWE) structure, enabling the claims to be digitally signed or MACed and/or encrypted.
 * So a JWT is a JWS structure with a JSON object as the payload. Some optional keys (or claims) have been defined such as iss, aud, exp etc.
 * This also means that its integrity protection is not just limited to shared secrets but public/private key cryptography can also be used.
 * */

@SpringBootApplication//(exclude = {
			// SecurityAutoConfiguration.class,
			//})
public class SpringJWTAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJWTAuthenticationApplication.class, args);
	}
}
