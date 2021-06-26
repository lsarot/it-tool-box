package com.example.springwebdemo.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration //for Java-based configuration of Spring beans
public class WebConfig {

	/* *** Bootstrapping Using spring-webmvc ***
	 * 
	 * Si usáramos solamente <artifactId>spring-webmvc</artifactId> y no spring-boot-starter-web
	 * Tendríamos que usar
	 * 
	 * @Configuration
	 * @EnableWebMvc		//configuration such as setting up the dispatcher servlet, enabling the @Controller and the @RequestMapping  annotations and setting up other defaults.
	 * @ComponentScan(basePackages = "com.baeldung.controller")
	 * 
	 * and create this initializer
	 * 
	 * This class can entirely replace the web.xml file from <3.0 Servlet versions.
	 *
	 * Ya la tenemos creada en este proyecto, sólo pq vimos que nos sirvió para mapear a páginas web del proyecto, 
	 * pero lo suyo es buscar cómo se hace con spring-boot-starter-web
	 * 
	 * 
	 * public class AppInitializer implements WebApplicationInitializer {
	 *    @Override
	 *    public void onStartup(ServletContext container) throws ServletException {
	 *        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
	 *        context.scan("com.baeldung");
	 *        container.addListener(new ContextLoaderListener(context));
	 * 
	 *        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
	 *        dispatcher.setLoadOnStartup(1);
	 *        dispatcher.addMapping("/");   
	 *    }
	 * }
	 *
	 * anteriormente configurabas por xml, y en esta clase cargabas la configuración desde el xml.
	 * */
	
	
}
