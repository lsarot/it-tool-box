package com.example.springwebdemo.misc.beanslesson;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.example.springwebdemo.service.EmployeeService;

/* There're several ways to configure beans in a Spring container. 
 * We can declare them using XML configuration. 
 * We can declare beans using the @Bean annotation in a configuration class.
 * Or we can mark the class with one of the annotations from the org.springframework.stereotype package and leave the rest to component scanning.
 */
/** THE MAIN TYPES ( @Component, @Service, @Configuration, @Repository, @Controller ) ARE AUTOMATICALLY LOADED IN APP CONTEXT.
 * @Bean/s aren't. @Bean/s are over methods which return something, in @Configuration marked classes.
 * 
 * We use @Configuration alongside with @ComponentScan which configures which packages to scan for classes with @Configuration
 * We can also use @ComponentScan without @Configuration
 * */


@Configuration //We should avoid putting the @Configuration class in the default package (i.e. by not specifying the package at all). In this case, Spring scans all the classes in all jars in a classpath. That causes errors and the application probably doesn't start.
@ComponentScans({ 
	//@ComponentScan //empty means start on this package
	@ComponentScan(basePackages = {"com.example.springwebdemo"}), //(basePackages or value).. base package to start scanning
	//@ComponentScan(basePackageClasses = {EmployeeService.class}) //specific classes
	//EXCLUSIONES
	@ComponentScan(excludeFilters = {
			@ComponentScan.Filter(type = FilterType.REGEX, pattern="com\\.baeldung\\.componentscan\\.springapp\\.flowers\\..*"),
			@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EmployeeService.class)
	})
	})
public class BeanAnnotations {

	
	@Component(value = "customizedBeanName")//by default will use className
	public class ComponentsLesson {
		
		public String getStringBean() {
			return "string de prueba";
		}
	}
	
	
	/**
	 * Can contain bean definition methods annotated with @Bean
	 * */
	@Configuration
	public class ConfigurationTypes {}
	
	
	/**
	 *	When using a persistence framework such as Hibernate, native exceptions thrown within classes annotated with @Repository will be automatically translated into subclasses of Spring's DataAccessExeption.
	 * To enable exception translation, we need to declare our own PersistenceExceptionTranslationPostProcessor bean:
	 * @Bean public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	 * 			return new PersistenceExceptionTranslationPostProcessor();
	 *	}
	 * */
	@Repository
	public class RepositoryTypes {}
	
	
	/**
	 * Just indicate that a class belongs to that layer
	 * */
	@Service
	public class ServiceTypes {}
	
	
	/**
	 * Tells the Spring Framework that this class serves as a controller in Spring MVC:
	 * */
	@Controller
	public class ControllerTypes {}
	
	
	/* Stereotype Annotations and AOP
	 * 
	 * suppose we want to measure the execution time of methods from the DAO layer.
	 * We'll create the following aspect (using AspectJ annotations) taking advantage of @Repository stereotype:
	 * 
	 * We created a pointcut that matches all methods in classes annotated with @Repository. We used the @Around advice to then target that pointcut and determine the execution time of the intercepted methods calls.
	 * Using this approach, we may add logging, performance management, audit, or other behaviors to each application layer.
	 * 
	@Aspect
	@Component
	public class PerformanceAspect {
	    @Pointcut("within(@org.springframework.stereotype.Repository *)")
	    public void repositoryClassMethods() {};
	 
	    @Around("repositoryClassMethods()")
	    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) 
	      throws Throwable {
	        long start = System.nanoTime();
	        Object returnValue = joinPoint.proceed();
	        long end = System.nanoTime();
	        String methodName = joinPoint.getSignature().getName();
	        System.out.println(
	          "Execution of " + methodName + " took " + 
	          TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
	        return returnValue;
	    }
	}
	*/
	
	
}

