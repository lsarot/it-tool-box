package com.example.codigosbasicos_springboot.topics.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/** https://www.baeldung.com/spring-boot-logging
 * 
 * *** SPRINGBOOT uses LOGBACK by DEFAULT and Apache

Zero Configuration Logging
In the case of logging, the only mandatory dependency is Apache Commons Logging.
We need to import it only when using Spring 4.x (Spring Boot 1.x), since in Spring 5 (Spring Boot 2.x) it's provided by Spring Framework’s spring-jcl module. (Aunque parece que sigue usando SLF4J y no JCL)
We shouldn't worry about importing spring-jcl at all if we're using a Spring Boot Starter (which almost always we are). That's because every starter, like our spring-boot-starter-web, depends on spring-boot-starter-logging, which already pulls in spring-jcl for us.
When using starters, Logback is used for logging by default.
Spring Boot pre-configures it with patterns and ANSI colors to make the standard output more readable.

 * *** LOG4J2 CONFIGURATION

In order to use any logging library other than Logback, though, we need to exclude it from our dependencies (spring-boot-starter-logging), then add spring-boot-starter-log4j2
We need to place in the classpath a file named like one of the following:
    log4j2-spring.xml    log4j2.xml
 * */
/* WAYS TO CHANGE LOG LEVEL

* BEFORE BOOTSTRAP *
-Setting as env variable
	_JAVA_OPTIONS=-Dlogging.level.org.springframework=TRACE,-Dlogging.level.com.baeldung=TRACE
-application.properties (better if we take properties externally, not from jar, so we can change them later)
	logging.level.root=WARN
	logging.level.com.baeldung=TRACE

* ON BOOTSTRAP *
-Using Maven
 	mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE
-Using Gradle
	./gradlew bootRun -Pargs=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE
-Using Java
	java -jar target/spring-boot-logging-0.0.1-SNAPSHOT.jar --trace
		-on our VM options
		--Dlogging.level.org.springframework=TRACE --Dlogging.level.com.baeldung=TRACE

* DURING JAR PACKAGING *
-In our logging framework configuration file
		When a file in the classpath has one of the following names, Spring Boot will automatically load it over the default configuration:
    	logback-spring.xml (recommended)    logback.xml    logback-spring.groovy    logback.groovy
 */

@RestController
public class LoggingController {
 
    Logger logger = LoggerFactory.getLogger(LoggingController.class); //SLF4J facade
    //Logger logger = LogManager.getLogger(LoggingController.class); //native LOG4J2
    			//We can now exploit the brand new features of Log4j2 without being stuck with the old SLF4J interface, but we're also tied to this implementation, and we'll need to rewrite our code when deciding to switch to another logging framework.
 
    //Con SLF4J no podemos setear level en código, por eso usamos la impl para setearlo directamente (cuando usamos Logback pq necesita SLF4J :
			    //ch.qos.logback.classic.Logger parentLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.logback");
			    //parentLogger.setLevel(Level.INFO);
    //Tomar el contexto del ROOT logger
			    //Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
			    //rootLogger.setLevel(Level.ERROR);
    
    
    @RequestMapping("/")
    public String index() {
        logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message");
        logger.warn("A WARN Message");
        logger.error("An ERROR Message");
 
        return "Howdy! Check out the Logs to see the output...";
    }
}
