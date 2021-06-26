package com.example.springwebdemo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * SPRING NORMAL PROJECT, NOT SPRING-BOOT WEB PROJECT
 * ESTA CLASE SE USA SI NO TRABAJAS CON SPRING-BOOT-STARTER-WEB, SINO SÓLO SPRING-WEBMVC ARTIFACT
 * 
 * web.xml vs Initializer with Spring **ver al final
 * */
/*
 * COMENTAMOS EL IMPLEMENTS PQ HACE QUE SE CONSIDERE DURANTE EL DESPLIEGUE EN EL SERVIDOR.. AHORA ES SÓLO INFORMATIVA LA CLASE!
 * PERO NO ESTÁ SIRVIENDO PARA DIRIGIR A PÁGINAS WEB DE NUESTRA APP
 * */
public class AppInitializer 
			implements WebApplicationInitializer {
	 
    @Override
    public void onStartup(ServletContext container) throws ServletException {
    	
    	/// 100% JAVA SERVLET CONFIGURATION
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //This type of context can then be configured registering a configuration class
        //context.register(AppConfig.class);
        //Or setting an entire package that will be scanned for configuration classes
        //context.setConfigLocation("com.example.app.config");
        context.scan("com.example.springwebdemo");
        
        
        //SE COMENTA PQ PARECE QUE TRAE PROBLEMA EN EL DESPLIEGUE
        //container.addListener(new ContextLoaderListener(context));
        //y esta podía ser una solución, pero quitar la de arriba sirvió!
        //container.setInitParameter("contextConfigLocation", "<NONE>");

        
        
        //The next step is creating and registering our dispatcher servlet
        //ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(context));
        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        dispatcher.addMapping("/other");
        dispatcher.addMapping("/logout", "/logout-success");
        
        
        //---------------------------------------
        /* HYBRID APPROACH (leer abajo)
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
 
        ServletRegistration.Dynamic dispatcher = container
          .addServlet("dispatcher", new DispatcherServlet(context));
 
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        */
        
    }
}

/** web.xml vs Initializer with Spring
 * 
 * WEB.XML
 * - Antes se hacía por web.xml la configuración del servlet que recibía las peticiones para cargar una página
 * 
 * HYBRID CONFIG (just keep servlet config in a separated file dispatcher-config.xml, instead of the traditional web.xml)
 * - With the adoption of the version 3.0 of Servlet APIs, the web.xml file has become optional, and we can now use Java to configure the DispatcherServlet.
 * 
 * 100% JAVA
 * - With this approach our servlet is declared in Java, but we still need an XML file to configure it. With WebApplicationInitializer you can achieve a 100% Java configuration.
 * This time we will use an annotation based context so that we can use Java and annotations for configuration and remove the need for XML files like dispatcher-config.xml
 * 
 * */
