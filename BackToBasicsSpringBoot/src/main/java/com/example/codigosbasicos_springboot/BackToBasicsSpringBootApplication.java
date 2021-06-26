package com.example.codigosbasicos_springboot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.codigosbasicos_springboot.controller.service.ServiceToTryAnything;


/**
 * MOSTRAMOS
 * CommandLineRunner y ApplicationRunner interfaces
 * Obtener un @Service usando el Spring context
 * @Order Annotation from Spring
 * 
 * INICIAMOS SERVICE TO TRY ANYTHING donde probamos cualquier cosa
 * */


@SpringBootApplication
public class BackToBasicsSpringBootApplication implements CommandLineRunner, ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(BackToBasicsSpringBootApplication.class);
	
	//TESTEANDO @Order annotation
	@Autowired
	private List<Rating> ratings;
	
	
	
	public static void main(String[] args) {
		System.err.println("First it calls all CommandLineRunner and ApplicationRunner implementations and then continues to main method in main class.");
		
		ConfigurableApplicationContext context;
		//context = SpringApplication.run(BackToBasicsSpringBootApplication.class, args);
		context = new SpringApplicationBuilder()
				        .main(BackToBasicsSpringBootApplication.class)
				        .sources(BackToBasicsSpringBootApplication.class)
				        .profiles("production") //references application-production.properties file
				        .run(args);
		
		System.err.println("main method, after loading context.");
		
		
		//OBTENEMOS UN MANAGED SERVICE
		HelloService service =  context.getBean(HelloService.class);
		logger.info(service.getMessage());
		
		
		logger.info("NOW WE TRY SOME LIBRARIES OR ANYTHING!  -->");
		//PROBAMOS LO QUE QUERAMOS PROBAR EN ESTE PROYECTO!
		ServiceToTryAnything service0 = context.getBean(ServiceToTryAnything.class);
		service0.run();
	}
	
	

	/** @Order -> SI USAMOS UN NÚMERO MENOR SE CARGA PRIMERO EN CONTEXTO.. EN EL CASO DE runners LO LLAMARÁ ANTES O DESPUÉS SEGÚN EL ORDEN.
	 * HIGHEST PRECEDENCE ES MUY NEGATIVO, LO CARGA ANTES. LOWEST PRECEDENCE ES MUY POSITIVO, LO CARGA DE ÚLTIMO
	 * */
	@Override
	@Order(Ordered.LOWEST_PRECEDENCE)//order of loading into context
	public void run(String... args) throws Exception {//CommandLineRunner
		System.err.println("run from CommandLineRunner");
		logger.info("2. Executing BackToBasicsSpringBootApplication run method due to CommandLineRunner interface");
		
		String strArgs = Arrays.stream(args).collect(Collectors.joining("|"));
		logger.info("Application started with arguments: " + strArgs);
	}

	
	@Override
	@Order(100)
	public void run(ApplicationArguments args) throws Exception {//ApplicationRunner
		System.err.println("run from ApplicationRunner");
		logger.info("3. Executing BackToBasicsSpringBootApplication run method due to ApplicationRunner interface");
		
		String strArgs = Arrays
				.stream(args.getSourceArgs())
				.collect(Collectors.joining("|"));
		logger.info("Application started with arguments: " + strArgs);
		
		//MOSTRANDO ITEMS EN LISTA CON OBJETOS INYECTADA USANDO JAVA 8 STREAMS
		
		strArgs = Arrays
				.stream(ratings.toArray())
				.map(r -> Integer.toString(((Rating)r).getRating()))
				.collect(Collectors.joining("|"));
		logger.info(strArgs);

		Object[] array = ratings
				.parallelStream()
				.map(r -> r.getRating())
				.toArray();
		logger.info(Arrays.asList(array).toString());
	}
	
	
	//---------------------------------------------------------------------------------------------
	
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Component
	public class MyRunnerBean implements CommandLineRunner {
		@Override
		@Order(Ordered.LOWEST_PRECEDENCE)
		public void run(String... args) throws Exception {
			System.err.println("run from CommandLineRunner inner class");
			logger.info("1. Executing MyRunnerBean run method due to CommandLineRunner interface");;
			//CUALQUIER SPRING STEREOTYPE PUEDE USAR CommandLineRunner o ApplicationRunner
			//@SpringBootApplication, @Component, @Service, etc.
			//ambas cumplen la misma función
			//será llamado su método run antes de que Spring termine de cargarse
			//sirve para cargar algún servicio costoso en procesamiento
		}
	}
	
	
	@Service
	public class HelloService {
		public String getMessage(){
			return "Hello World!";
		}
	}
	
	//--------------------------------------------------------------------------------------------- TEST @Order TAG
	
	public interface Rating {
	    int getRating();
	}
	@Component
	@Order(1)//A NIVEL DE CLASE SÍ FUNCIONA.. AL AUTOINYECTAR EN UNA LISTA, SE ORDENAN POR PRIORIDAD, MENOS ES MÁS IMPORTANTE!
	public class Excellent implements Rating {
	    @Override public int getRating() {return 1;}
	}
	@Component
	@Order(2)
	public class Good implements Rating {
	    @Override public int getRating() {return 2;}
	}
	@Component
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public class Average implements Rating {
	    @Override public int getRating() {return 3;}
	}
	
	
}
