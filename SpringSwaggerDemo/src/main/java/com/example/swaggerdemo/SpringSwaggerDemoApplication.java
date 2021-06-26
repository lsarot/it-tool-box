package com.example.swaggerdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** http://springfox.github.io/springfox/docs/current/#maven
 * https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * 
 * Siendo SpringBoot sólo necesito 'io.springfox : springfox-boot-starter : 3.0.0'
 * Configurar el bean tipo Docket, y debo poder acceder a las sigs urls:
http://localhost:8008/v2/api-docs
http://localhost:8008/swagger-resources
http://localhost:8008/swagger-resources/configuration/ui
http://localhost:8008/swagger-resources/configuration/security

 * Si no usamos el starter de springboot. After adding 'springfox-swagger-ui' artifact to pom:
http://localhost:8008/swagger-ui.html    NO FUNCIONA YA
http://localhost:8008/swagger-ui/index.html    FUNCIONA

 * Usar group en todas las urls si fue configurado en el Docket bean:   ?group=api-group-name(com.leo)

 * Antes de descubrir en la doc oficial que cambiaron el url dentro del webjar, de /swagger-ui.html  a /swagger-ui/index.html: 
 * FUNCIONA AL CONFIGURAR CON 2.9.2, quedando en memoria caché del navegador y luego funciona al pasar a la 3.0.0, pero al abrir en otra ventana incógnito no funciona!
 * 
 * Funcionaba!, pero por cambio de versiones o modificación de los webjars puede dejar de funcionar por cualquier cosa!!!
 * 
 * LEVANTAR PRIMERO EL PROYECTO 'demo-spring-sec-joinasrs'
 * Levantar este proyecto
 * Ir a la url: http://localhost:8080/swagger-ui.html
 * 
 * notas: 
 * .el regex del email en User puede fallar!
 * .si usamos el botón Authorize, aunque no hace falta, hay un error de CORS que no pudimos resolver!
 * */
 
@SpringBootApplication
public class SpringSwaggerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSwaggerDemoApplication.class, args);
	}

}
