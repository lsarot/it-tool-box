package com.example.springsecdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/* ESTE PROYECTO TRABAJA CON OAUTH2
 ----------------------------
 HICIMOS VARIAS PRUEBAS:
 
 En OAuth2 existen cuatro roles:
 RO: resource owner
 RS: resource server
 AS: authentication server
 C: client
 
 1. (OAuth2) [THIS project] usamos un AS externo de gran capacidad (KeyCloak embedded in a Spring Boot app) (oauth-authorization-server), RS Spring Boot que expone algún endpoint con info del RO (esta app), Cliente web Angular.
 			Siendo este el RS, no compartimos la info del RO, pero pusimos unos endpoints Rest sólo por mostrar algo.
 
 2. (OAuth2) [SpringOauthJoinRSwithAS project] AS y RS en el mismo Spring Boot app (AS es una impl de Spring que parece más básica que KeyCloak), Cliente es otra app Spring Boot (que tiene un endpoint para acceder vía web, y su back consume del RS datos del RO) (SpringOauthClient project)
 
 3. (OAuth2) [SpringOauthClient2 project] se autentica con un external OAuth provider, such as GitHub or Google
 
 4. (JWT) [SpringJWTAuthentication] este muestra, sin usar OAuth, como autenticar con un JWT al cliente. Recordar que JWT lo usa OAuth, pero no es propio de él.
 
 ---------------------------- 
 * 
 * https://www.ionos.es/digitalguide/servidores/seguridad/oauth-y-su-version-oauth2/
 * 
 En OAuth2 existen cuatro roles:
  *  (RO) Resource owner (user o usuario): entidad que concede a un cliente acceso a sus datos protegidos (recursos).
  *  (RS) Resource server (servicio): un servidor en el que se almacenan los datos protegidos del resource owner.
  *  (AS) Authorization server: un servidor que autentica al resource owner y emite un access token temporal para un ámbito (scope) definido por el propietario del recurso. En la práctica, el authorization server y el resource server se utilizan a menudo juntos y se denominan también OAuth server.
  *  (C) Cliente (third-party o tercero): una aplicación de escritorio, web o móvil que desea acceder a los datos protegidos del resource owner.
 
  * Diferenciamos 4 flujos distintos que podemos utilizar: (GRANT-TYPES) 
		Código de autorización: (authorization-code) el cliente solicita al resource owner que inicie sesión en el authorization server. El resource owner es, en ese momento, redirigido al cliente junto con un código de autorización. Ese código sirve para que el authorization server emita un access token para el cliente.
		Autorización implícita (implicit authorization): este proceso de autorización se parece bastante a la autorización mediante código que acabamos de comentar, pero es menos complejo porque el authorization server emite el access token directamente.
		Credenciales de contraseña de propietario del recurso (resource owner password credentials): en este caso, el resource owner confía sus datos de acceso directamente al cliente, algo que es directamente contrario al principio básico de OAuth, pero que implica menos esfuerzo para el resource owner.
		Credenciales de cliente (client credentials): este proceso de autorización es especialmente sencillo y se utiliza cuando un cliente quiere acceder a datos que no tienen propietario o que no requieren autorización.
 
 * Common Flow: (del de código de autorización)
 C, if it doesn't have a stored token, ask AS for one, giving credentials. Receives a code, and then exchanges the code for a token.
 C, having a token, makes a request to RS, sending token as credential.
 RS asks AS if token is valid, and if it is, responds to client with OK.
 Now C can ask RS for RO data.
 			** En caso de que un cliente necesite obtener acceso a los datos protegidos del resource owner en el futuro, podrá utilizar un refresh token, que tiene una duración limitada pero dura más que el access token, para solicitar un nuevo access token al authorization server. El resource owner no tiene que dar una nueva autorización.
 */
/**	** THIS PROJECT **
 * AS uses port 8083
 * RS uses port 8081 (THIS APP)
 * Client ANGULAR uses port 8089
 *
 * Spring REST API + OAuth2 + Angular
 * https://www.baeldung.com/rest-api-spring-oauth2-angular
 * 
 * OAuth makes life simpler and safer for businesses and their end users, allowing them to bypass standard credential management in favor of logging in via another site’s credentials—their Facebook username and password, for instance.
 * 
   Authorization Server (KeyCloak server running in a SpringBoot App). Esto usualmente lo ofrecen servicios como Google, Facebook, etc donde los usuarios acceden a tu servicio con credenciales de ellos.
   Resource Server (esta app, que ofrece un API)
   UI authorization code – a front-end application using the Authorization Code Flow (el cliente, una app Angular sencilla -> oauth-ui-authorization-code-angular)
 *
 * 
 * Previously, the Spring Security OAuth stack offered the possibility of setting up an Authorization Server as a Spring Application. 
 * But the project has been deprecated, mainly because OAuth is an open standard with many well-established providers 
 * such as Okta, Keycloak, and Forgerock to name a few.
 *	Of these, we'll be using Keycloak. It's an open-source Identity and Access Management server administered by RedHat, developed in Java, by JBoss. It supports not only OAuth2 but also other standard protocols such as OpenID Connect and SAML.
 * 
 * Relevant information:

   oauth-authorization-server is a Keycloak Authorization Server wrapped as a Spring Boot application
   There is one OAuth Client registered in the Authorization Server:
       Client Id: newClient
       Client secret: newClientSecret //adfc14bb-b57b-4902-b694-af15ec5d3a34
       Redirect Uri: http://localhost:8089/
   There are several users registered in the Authorization Server:
       john@test.com / 123
       mike@other.com / pass

 * */
/* PASSWORD STORAGE HISTORY (why the 1sec tunning for validating a psw ?)
https://docs.spring.io/spring-security/site/docs/current/reference/html5/

Throughout the years the standard mechanism for storing passwords has evolved. In the beginning passwords were stored in plain text. The passwords were assumed to be safe because the data store the passwords were saved in required credentials to access it. However, malicious users were able to find ways to get large "data dumps" of usernames and passwords using attacks like SQL Injection. As more and more user credentials became public security experts realized we needed to do more to protect users' passwords.
Developers were then encouraged to store passwords after running them through a one way hash such as SHA-256. When a user tried to authenticate, the hashed password would be compared to the hash of the password that they typed. This meant that the system only needed to store the one way hash of the password. If a breach occurred, then only the one way hashes of the passwords were exposed. Since the hashes were one way and it was computationally difficult to guess the passwords given the hash, it would not be worth the effort to figure out each password in the system. To defeat this new system malicious users decided to create lookup tables known as Rainbow Tables. Rather than doing the work of guessing each password every time, they computed the password once and stored it in a lookup table.
To mitigate the effectiveness of Rainbow Tables, developers were encouraged to use salted passwords. Instead of using just the password as input to the hash function, random bytes (known as salt) would be generated for every users' password. The salt and the user’s password would be ran through the hash function which produced a unique hash. The salt would be stored alongside the user’s password in clear text. Then when a user tried to authenticate, the hashed password would be compared to the hash of the stored salt and the password that they typed. The unique salt meant that Rainbow Tables were no longer effective because the hash was different for every salt and password combination.
In modern times we realize that cryptographic hashes (like SHA-256) are no longer secure. The reason is that with modern hardware we can perform billions of hash calculations a second. This means that we can crack each password individually with ease.
Developers are now encouraged to leverage adaptive one-way functions to store a password. Validation of passwords with adaptive one-way functions are intentionally resource (i.e. CPU, memory, etc) intensive. An adaptive one-way function allows configuring a "work factor" which can grow as hardware gets better. It is recommended that the "work factor" be tuned to take about 1 second to verify a password on your system. This trade off is to make it difficult for attackers to crack the password, but not so costly it puts excessive burden on your own system. Spring Security has attempted to provide a good starting point for the "work factor", but users are encouraged to customize the "work factor" for their own system since the performance will vary drastically from system to system. Examples of adaptive one-way functions that should be used include bcrypt, PBKDF2, scrypt, and argon2.
Because adaptive one-way functions are intentionally resource intensive, validating a username and password for every request will degrade performance of an application significantly. There is nothing Spring Security (or any other library) can do to speed up the validation of the password since security is gained by making the validation resource intensive. Users are encouraged to exchange the long term credentials (i.e. username and password) for a short term credential (i.e. session, OAuth Token, etc). The short term credential can be validated quickly without any loss in security.
 * */
@SpringBootApplication(
		exclude = HibernateJpaAutoConfiguration.class
				//, SecurityAutoConfiguration.class
				//, OAuth2AutoConfiguration.class
		)
public class SpringSecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityDemoApplication.class, args);
	}
}
