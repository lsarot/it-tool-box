package com.example.springwebdemo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import com.example.springwebdemo.configuration.datasource.DBConfig_1_Source;
import com.example.springwebdemo.misc.beanslesson.TestSomeBeans;
import com.example.springwebdemo.model.persistence.dao.EmployeeDao;
import com.example.springwebdemo.model.persistence.entity.Employee;
import com.example.springwebdemo.service.EmployeeService;

/**
 * mvn clean -Dmaven.test.skip=true install   creará el .war.. lo desplegamos en Tomcat externo.
 * o directo desde Eclipse, lo despliega en su instancia sin ningún paso extra y usa hot-swap para redesplegar al hacer otro build.
 * 
 * *** ESTE PROYECTO NOS SERVIRÁ PARA PROBAR N COSAS DE SPRING Y TOMAR NOTAS DE LAS ETIQUETAS IGUAL.
 * JNDI + Hibernate + H2 DB + N DataSources
 * Spring Security
 * 			Http Basic or Digest, formLogin, methodLevel sec, 
 * 			Oauth2, SsO, SAML, ... en el otro proyecto demo-spring-sec y demo-spring-sec-oauth-joinasrs
 * 
 * 
 * *** NOTA: NOS EMPEZÓ A DAR FALLO EL JNDI DATASOURCE AL MONTAR EN TOMCAT EXTERNO (no siempre!!), PERO DESPLEGANDO TOMCAT POR FUERA, SI LO HACEMOS POR ECLIPSE NO HUBO FALLO.
 * el fallo es que no recupera nada en la consulta, devuelve una lista vacía..
 * 
 * 
 * *** JNDI RESOURCES ***
 * SE NOTÓ QUE SE ELIMINARON REPENTINAMENTE LOS RECURSOS PUESTOS SOBRE server.xml y context.xml.
 * Si falla el despliegue, revisar que estén creados. 
 * guía: CONFIGURE JNDI DATASOURCE ON SPRING.txt
 * guía: CONFIGURE Tomcat+HikariCP.txt
 * */

@SpringBootApplication//(exclude = {
		  // SecurityAutoConfiguration.class, //TO DISABLE SPRING SECURITY AUTO-CONFIG
		  // ManagementWebSecurityAutoConfiguration.class //ESTA también si falla!
		  //}
		  //(scanBasePackages = "com.example.springwebdemo")
public class SpringWebDemoApplication 
		extends SpringBootServletInitializer 
				implements CommandLineRunner, EnvironmentAware {

	//-------------------------------------------
	
	/**
	 * When we want to deploy in an external container
	 * */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		System.out.println("\n** DEPLOYING ON EXTERNAL CONTAINER **\n");
		return application.sources(SpringWebDemoApplication.class);
	}
	
	//-------------------------------------------
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n* IN COMMAND LINE RUNNER RUN *\n");
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		//// Así se recibía Environment antes. Ahora usamos inyección con @Autowired
	}
	
	
	private static ApplicationContext context;
	
	
	public static void main(String[] args) {
		context = SpringApplication.run(SpringWebDemoApplication.class, args);
		
		System.out.println("\n** DEPLOYING ON EMBEDDED CONTAINER **\n");
		
		gettingEnvironmentVarsOrSystemProps();

		usingSeveralDataBases();
		
		TestSomeBeans test0 = new TestSomeBeans();
		test0.testSomeBeans((ApplicationContext)context);
		
		checkBeansPresence("springWebDemoApplication", "employeeDao", "nothing");//by default (if we don't use a name), the bean will be registered with its className (not ClassName)
		//showAllBeansRegistered();
		
	}
	
	
	private static void usingSeveralDataBases() {
		try {
			EmployeeDao edao = new EmployeeDao();

			//este toma un datasource directamente de una clase que lo instancia (medio singleton pero no!) y se lo pasa a un método del dao.
			System.out.print("\nFirst DataSource:\n");
			for (Employee emp : edao.fetchAll_WithDatasource( DBConfig_1_Source.getDataSource() )) {
				System.out.println(emp);
			}
			
			//OBTENEMOS UN MANAGED SERVICE
			//se suele inyectar, pero lo tomamos del context ya que es static el método!
			EmployeeService empSrv = context.getBean(EmployeeService.class);

			//este usando Spring, toma un servicio declarado, el cual se instancia con un dao, y el dao se instancia con un datasource
			System.out.print("\nSecond DataSource:\n");
			for (Employee emp : empSrv.findAll_using_h2_1()) {
				System.out.println(emp);
			}
			
			
			//TAMBIÉN SE REGISTRÓ EN EL CONTENEDOR OTRO DATASOURCE SIN USAR ("H2_2")
			

			System.out.println("\nThird DataSource (JNDI based)(declared in the app itself [embedded Tomcat] Or in the external Tomcat) :\n");
			//for (Employee emp : edao.fetchAll( context.getBean("jndi-datasource", DataSource.class) )) {
			for (Employee emp : empSrv.findAll_using_jndi_datasource()) {	
				System.out.println(emp);
			}
			
			
			System.out.println("\nTry some Hibernate Features\n");
			empSrv.findAll_using_hibernate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void gettingEnvironmentVarsOrSystemProps() {
		//GETTING ENVIRONMENT VARIABLE, OR -FLAG PASSED ON STARTUP COMMAND
		System.out.println( System.getenv("PATH") );
		//GETTING SYSTEM PROPERTY
		System.out.println( System.getProperty("java.class.path", "not defined") );
		//System.setProperty("prop.A", "propiedad1");
		
		ConfigurableEnvironment env = (ConfigurableEnvironment) context.getEnvironment();
		//ALSO CAN GET SYSTEM PROPERTY FROM HERE, BUT ENV CONTAINS OTHERS LIKE THOSE IN .properties FILE (if we loaded it)
		String prop = env.getProperty("prop.A", "not found");
		System.out.println("Tried to retrieve from .properties file: " + prop);
	}


    private static void checkBeansPresence(String... beans) {
    	System.out.println();
        for (String beanName : beans) {
            System.out.println("Is " + beanName + " in ApplicationContext: " + context.containsBean(beanName));
        }
        System.out.println();
    }
    
    private static void showAllBeansRegistered() {
    	List<String> beans = Arrays.asList(context.getBeanDefinitionNames());
    	Collections.sort(beans);
    	for (String beanName : beans) {
    		System.out.println(beanName + " -> " + context.getBean(beanName).getClass().getName());
    	}
    }

}

/* SPRING BOOT OR SPRING MVC ??
 * Crea un proyecto SpringBoot y agrégale spring-web que incluye SpringMVC, o seguramente lo incluye spring-boot-starter-web
 * 
    Spring MVC is a complete HTTP oriented MVC framework managed by the Spring Framework and based in Servlets. 
    It would be equivalent to JSF in the JavaEE stack. 
    The most popular elements in it are classes annotated with @Controller, where you implement methods you can access using different HTTP requests. It has an equivalent @RestController to implement REST-based APIs.
    Spring MVC is a framework to be used in web applications.
    
    Spring boot is a utility for setting up applications quickly, offering an out of the box configuration in order to build Spring-powered applications. 
    As you may know, Spring integrates a wide range of different modules under its umbrella, 
    as spring-core, spring-data, spring-web (which includes Spring MVC, by the way) and so on. 
    With this tool you can tell Spring how many of them to use and you'll get a fast setup for them (you are allowed to change it by yourself later on).
	SpringBoot is a Spring based production-ready project initializer.
	Spring boot = Spring MVC + Auto Configuration(Don't need to write spring.xml file for configurations) + Server(You can have embedded Tomcat, Netty, Jetty server).
	And Spring Boot is an OPINIONATED FRAMEWORK, so its build taking in consideration for fast development, less time need for configuration and have a very good community support. 
 * */

/** SPRING ANNOTATIONS
 *
   	In most typical applications, we have distinct layers like data access, presentation, service, business, etc.
	And, in each layer, we have various beans. Simply put, to detect them automatically,  Spring uses classpath scanning annotations.
	
	Then, it registers each bean in the ApplicationContext.
	When we annotate a class for auto-detection, then we should use the respective stereotype.	
	Here's a quick overview of a few of these annotations:
	
    @Component is a generic stereotype for any Spring-managed component. 
    	CUANDO NO ESTÁ CLASIFICADO. Las demás annotations usan @Component también
    @Service annotates classes at the service layer.
    	 To indicate that it's holding the business logic. So there's no any other specialty except using it in the service layer.
    @Repository annotates classes at the persistence layer, which will act as a database repository
		To catch persistence specific exceptions and rethrow them as one of Spring’s unified unchecked exception. For this Spring provides PersistenceExceptionTranslationPostProcessor.
	@Configuration to configure Beans that will be injected.
	@Bean
	@Controller & @RestController for web services, @RestController is @Controller + @ResponseBody.
	@Profile
	
	ver clase BeanAnnotations.class de este proyecto!!!
 * */

