package com.leo.spring.cloud.springconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/* https://www.baeldung.com/spring-cloud-configuration */
/**
 * Spring Cloud Config is Spring's client/server approach for storing and serving distributed configurations across multiple applications and environments.
 * 
 * This configuration store is ideally versioned under Git version control and can be modified at application runtime. 
 * 
 * It can be used in any environment running any programming language.
 * */
/* The API provided by this server let us query the properties by means of this paths:

	/{application}/{profile}[/{label}]
	/{application}-{profile}.yml
	/{label}/{application}-{profile}.yml
	/{application}-{profile}.properties
	/{label}/{application}-{profile}.properties
	
	were label refers to the git branch, application to the client's app name (spring.application.name) and profile to client's current active profile.
	ig:   curl http://root:s3cr3t@localhost:8888/config-client/development/master
	http://localhost:8888/master/config-client-development.properties
 */

//@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class SpringConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringConfigServerApplication.class, args);
	}

}
