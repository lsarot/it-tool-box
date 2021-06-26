package com.example.springwebdemo.configuration.datasource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;


/** 
 * PODEMOS USAR CACHE DISTRIBUIDA TIPO REDIS O HAZELCAST PARA SUSTITUIR JNDI
 * 
 * ESTA CLASE MUESTRA CÓMO REGISTRAR Y RECUPERAR UN DATASOURCE JNDI
 * 
 * EL REGISTRO SE PROBÓ INCLUSO SOBRE EL CONTENEDOR EMBEBIDO DE SPRINGBOOT (TOMCAT)
 * 
 * EL PROBLEMA ES QUE SE REGISTRA UN DATASOURCE PERO LO HACE EL CONTENEDOR, NO ES QUE LE PASAMOS EL OBJETO Y LE ASIGNAMOS UN NOMBRE
 * ENTONCES NO PODEMOS ELEGIR USAR EL NUESTRO QUE ES UN HIKARI CP SINO QUE SE LE PASAN PARÁMETROS COMO DRIVER, POOLFACTORY, ETC.
 * 
 * *** REGISTRAR EL DATASOURCE CON JNDI EN UN TOMCAT EMBEBIDO NO TIENE SENTIDO, SE RECOMIENDA USAR IoC Y QUE QUE SPRING MANEJE LA DEPENDENCIA.
 * AHORA, SI DESPLEGAMOS EN UN CONTENEDOR (TOMCAT) EXTERNO, NO USAMOS EL MÉTODO (que registra Bean) TomcatServletWebServerFactory tomcatFactory(), PQ DABA FALLO.
 * 
 * ENTONCES, SI QUEREMOS COMPARTIR ENTRE N APPS UN RECURSO JNDI, DEBEMOS DESPLEGARLAS EN TOMCAT EXTERNO
 * y será por allí que configuremos el pool de conexiones JDBC
 * 
 * TOMCAT POR DEFECTO USA DBCP CONN POOL
 * PERO TODOS SON UNA LENTEJA ALADO DE HIKARI CP.
 * entonces DEBEMOS USAR HIKARI EN TOMCAT. https://liferay.dev/blogs/-/blogs/tomcat-hikaricp o buscar 'CONFIGURE Tomcat+HikariCP.txt'
 * 
 * 
 * ENTRE 
 * 1. USAR DBCP POOL EN TOMCAT EXTERNO, desplegando las apps como .war
 * O
 * 2. DESPLEGAR CADA UNA CON SU HIKARI CP, desplegando como .jar
 * MEJOR 2.
 * a menos que configuremos HIKARI en Tomcat externo.
 * 
 * */
/** Role of JNDI in Modern Application Architecture ***

While JNDI plays less of a role in lightweight, containerized Java applications such as Spring Boot, there are other uses. 
Three Java technologies that still use JNDI are JDBC, EJB, and JMS. All have a wide array of uses across Java enterprise applications.

For example, a separate DevOps team may manage environment variables such as username and password for a sensitive database connection in all environments.
 A JNDI resource can be created in the web application container, with JNDI used as a layer of consistent abstraction that works in all environments.
This setup allows developers to create and control a local definition for development purposes while connecting to sensitive resources in a production environment through the same JNDI name.
 * */
/* 
*** In general, services should store an object reference, serialized data, or attributes in a directory context. It all depends on the needs of the application.
*** Note that using JNDI this way is less common. Typically, JNDI interfaces with data that is managed outside the application runtime.
*** However, if the application can already create or find its DataSource, it might be easier to wire that using Spring. 
	  In contrast, if something outside of our application bound objects in JNDI, then the application could consume them.
 */

@Configuration
@PropertySources(value = {
		//@PropertySource("file:src/main/resources/h2_1.properties")
})
public class DBConfig_JNDI_Source {
   
	@Autowired
   private Environment env;   
   
	/**
	 * ESTO CONFIGURA EL EMBEDDED TOMCAT PARA QUE USE JNDI Y CONFIGURAMOS UN DATASOURCE AHÍ.
	 * COMENTAMOS EL PRIMER @Bean PQ ESTABA DANDO FALLO SI DESPLEGAMOS EN UN TOMCAT EXTERNO, pq en ese caso debe desplegarse el dataSource en el contenedor externo!
	 * */
	/*
	 * Creamos un managed Bean (que luego recuperamos en la creación del dataSource) al cual el contenedor le pasa la instancia del Tomcat, y le activamos el NamingService de JNDI.
	 * Luego de que cargue el contexto, registramos un recurso, en este caso el data source.
	 */
	//@Bean
	public TomcatServletWebServerFactory tomcatFactory() {

		return new TomcatServletWebServerFactory() {

			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				try {
				//// ASÍ CONFIGURA UN H2 DATA SOURCE, CON TOMCAT CONN POOL
						
				ContextResource resource = new ContextResource();
				resource.setType(DataSource.class.getName());
				resource.setName("jdbc/myDataSource");
						resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory"); //factory="com.zaxxer.hikari.HikariJNDIFactory" para Hikari parece!
				resource.setProperty("driverClassName", "org.h2.Driver");
				resource.setProperty("url", "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");
				resource.setProperty("username", "user");
				resource.setProperty("password", "pass");
						//resource.setAuth("Container"); //no hacía falta
				
				context.getNamingResources().addResource(resource);

				
				
				//PERO SE ENCARGA EL TOMCAT DE LEVANTARLO.. 
				//... Y SI QUEREMOS INSTANCIARLO Y MANDÁRSELO ?? 
				//... el "factory" es el que nos hace ruído!, queremos mandar un objeto listo! Un HikariCP DataSource programmatically
						//ContextService ctxS = new ContextService(); no parece
						//resource.setProperty("objeto", ds); no parece
				
				
				
				// DIGAMOS QUE QUEREMOS REGISTRAR ESTE DATASOURCE
				//DataSource ds = DBConfig_1_Source.getDataSource(); //tomamos la de la otra clase
				
				
				//// *** PODEMOS CONFIGURAR TOMCAT EXTERNO CON HIKARI CP TAMBIÉN, SIN HACERLO PROGRAMÁTICAMENTE !
				
				
				//-----------------------
				// NO HA FUNCIONADO!!! intentar acoplar el jndi resource de esta manera, creándolo aquí!
				// NO ENCUENTRA CONTEXTO INICIAL 
				// Pensé que pudiera ser por hacerlo aquí porque todavía no estaba instanciado o algo así, ya que supuestamente con el método de arriba se implora que levante el servicio JNDI, pero no lo encuentra.
				// ..se intentó sacando el proceso de registrar en el contexto en otro método que se llama más tarde (en el main thread), y no funcionó, pero esta vez fue que no encontraba un contexto con x nombre
				// No se intentará más ya que igualmente es raro usar JNDI así!!
				// lo normal es sólo recuperar el objeto, pero la creación se hace sobre un contenedor compartido por N aplicaciones o en remoto, como se muestra abajo parece una configuración donde recupera el contexto remoto!.
				
				// javax.naming.NoInitialContextException: Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial
				
				//JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
				//bean.setJndiName("java:/comp/env/jdbc/myDataSource");
				//bean.setProxyInterface(DataSource.class);
				
						// bean.setBeanFactory(BeanFactory);
						// bean.setBeanClassLoader(ClassLoader);
						// Properties env = bean.getJndiEnvironment();
				
				//bean.setDefaultObject(ds);
				//bean.setExpectedType(DataSource.class);
				//JndiTemplate jndiTemplate = bean.getJndiTemplate();
				//jndiTemplate.bind("jdbc/myDataSource", ds);
						// InitialContext ctx = (InitialContext) jndiTemplate.getContext();
						// ctx.bind("java:comp/env/jdbc/myDataSource", ds);
				
				// NI ASÍ TAMPOCO !!
				//JndiTemplate jndiTemplate = new JndiTemplate();
				//InitialContext ctx = (InitialContext) jndiTemplate.getContext();
				//jndiTemplate.bind("java:comp/env/jdbc/myDataSource", ds);	
				
				/** PARECE QUE CON EL tomcat.enableNaming() del otro método se activa la implementación JNDI del tomcat embedded, pero no debemos registrar el bean en este método quizás
				 
				The javax.naming package comprises the JNDI API. Since it's just an API, rather than an implementation, you need to tell it which implementation of JNDI to use. The implementations are typically specific to the server you're trying to talk to.
				To specify an implementation, you pass in a Properties object when you construct the InitialContext. These properties specify the implementation to use, as well as the location of the server. The default InitialContext constructor is only useful when there are system properties present, but the properties are the same as if you passed them in manually.
				As to which properties you need to set, that depends on your server. You need to hunt those settings down and plug them in.
			 			Example:
			 			jndi.java.naming.provider.url=jnp://localhost:1099/
						jndi.java.naming.factory.url=org.jboss.naming:org.jnp.interfaces
						jndi.java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
			 
			    -- Instancia tomando un contexto inicial remoto:
			    -- You should set jndi.properties. I've given below some piece of code that explain how the properties are set for activemq. Like that you can set for your application. Inside a J2EE container like JBoss no need to set these properties.
					    Properties props = new Properties();
						props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
						props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
						InitialContext ctx = new InitialContext(props);
						// get the initial context
						// InitialContext ctx = new InitialContext();
						QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");        
						// create a queue connection
						QueueConnection queueConn = connFactory.createQueueConnection();   
						queueConn.start();
						// lookup the queue object
						Queue queue = (Queue) ctx.lookup("dynamicQueues/Payment_Check"); 
				 **/	
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	
	/**
	 * CON ESTO RECUPERAMOS YA SEA UN JNDI RESOURCE DENTRO DEL TOMCAT EMBEDDED O EL EXTERNO.
	 * */
	@Bean("jndi-datasource")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		//FUNCIONA!!!
		return (DataSource) new JndiTemplate().lookup("java:comp/env/jdbc/myDataSource");
		
		
		//FUNCIONA!!!
		//JndiTemplate jndiTemplate = new JndiTemplate();
		//return (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/myDataSource");

		
		/*FUNCIONA!!!
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName("java:/comp/env/jdbc/myDataSource");
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(true);//no funcionó con false
		bean.afterPropertiesSet();
		
		// bean.setCache(true);
		// bean.setExposeAccessContext(false);
		// bean.setDefaultObject(null);
		// bean.setBeanFactory(BeanFactory);
		// bean.setBeanClassLoader(ClassLoader);
		// Properties env = bean.getJndiEnvironment();
		
		//return (DataSource) bean.getObject();
		*/
		
		// JndiTemplate jndiTemplate = new JndiTemplate();
		// InitialContext ctx = (InitialContext) jndiTemplate.getContext();
		// JndiTemplate jndiTemplate = bean.getJndiTemplate();
		// InitialContext ctx = (InitialContext) jndiTemplate.getContext();
		// ctx.bind("java:comp/env/jdbc/datasource", ds);
		// DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/jndiDataSourceH2");
		
	}
	
}

/**
 * * * *  OTHER APPROACH TO RECOVER MORE THAN 1 INSTANCE OF A DATASOURCE USING JNDI * * *
 * */
/* CONFIGURATION TO USE MULTIPLE JNDI DATASOURCES ON SPRINGBOOT

-- example application.properties file:
spring.datasource.primary.jndi-name=java:/comp/env/jdbc/SecurityDS
spring.datasource.secondary.jndi-name=java:/comp/env/jdbc/TmsDS
//spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
//spring.jpa.show-sql=false

@Configuration
@EnableConfigurationProperties //CON ESTO LAS OBTENEMOS DE application.properties y nos evitamos problemas futuros al buscar otro .properties
public class AppConfig {

    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Bean public JndiPropertyHolder primary() {
        return new JndiPropertyHolder();
    }

    // Nótese que toma el primary directo del método, no del contexto del contenedor.
    // Quizás usa @Bean sobre el método primary sólo pq lo necesita @ConfigurationProperties.
    // Nótese tbm que no asigna un nombre al bean, y habrán 2 de ese tipo registrados en el contenedor.
    @Primary
    @Bean public DataSource primaryDataSource() {
        JndiTemplate jndi = new JndiTemplate();
        DataSource dataSource = (DataSource) jndi.lookup(primary().getJndiName());
        return dataSource;
    }

    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    @Bean public JndiPropertyHolder secondary() {
        return new JndiPropertyHolder();
    }

    @Bean public DataSource secondaryDataSource() {
        JndiTemplate jndi = new JndiTemplate();
        DataSource dataSource = (DataSource) jndi.lookup(secondary().getJndiName());
        return dataSource;
    }

    private static class JndiPropertyHolder {
        private String jndiName;
        public String getJndiName() {
            return jndiName;
        }
        public void setJndiName(String jndiName) {
            this.jndiName = jndiName;
        }
    }
}
*/

