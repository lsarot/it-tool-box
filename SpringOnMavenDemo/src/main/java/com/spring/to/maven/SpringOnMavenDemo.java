package com.spring.to.maven;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/* Spring versions & JDK versions 
Spring Framework 5.3.x: JDK 8-17 (expected) 
Spring Framework 5.2.x: JDK 8-15 (expected) 
Spring Framework 5.1.x: JDK 8-12 
Spring Framework 5.0.x: JDK 8-10 
Spring Framework 4.3.x: JDK 6-8 
Las versiones anteriores de Spring supongo que pedían mínimo JDK 5 */

public class SpringOnMavenDemo implements EnvironmentAware {

	/* NO se están inyectando
	@Autowired
	public Environment env;//no se está inyectando
	@Autowired
	private ConfigurableApplicationContext context;
	 */
	// ni recuperando aquí
	public void setEnvironment(Environment environment) {
		//// Así se recibía Environment antes. Ahora usamos inyección con @Autowired
	}

	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		SpringOnMavenDemo demo = new SpringOnMavenDemo();
		demo.start();
	}

	private void start() throws InterruptedException, IOException {
		
		//Servicio s = context.getBean(Servicio.class);
		//s.sayHi("Leo");
	}
	
	@Service
	public class Servicio {
		void sayHi(String name) {System.out.printf("Hi, %s", name);}
	}

	
}
