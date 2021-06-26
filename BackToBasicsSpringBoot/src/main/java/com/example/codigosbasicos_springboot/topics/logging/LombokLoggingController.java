package com.example.codigosbasicos_springboot.topics.logging;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;


/** LOMBOK es una librería que básicamente mantiene nuestro código limpio y sin mucha verbosidad.
 * 
 * Particularmente no me gusta porque deja warnings por todo el proyecto!
 * variable sin declarar; no hay constructor; no hay getters ni setters; ...
 * */
/* NO USAR JAVA.UTIL.LOGGING
 * Spring Boot also supports JDK logging, through the logging.properties configuration file.
 * There are known classloading issues with Java Util Logging that cause problems when running from an ‘executable jar'. We recommend that you avoid it when running from an ‘executable jar' if at all possible.
 * It's also a good practice, when using Spring 4, to manually exclude commons-logging in pom.xml, to avoid potential clashes between the logging libraries. Spring 5 instead handles it automatically, hence we don't need to do anything when using Spring Boot 2.
 * */

@RestController
//@Slf4j   // or @CommonsLog   to use a facade (will use Logback as impl)
@Log4j2  //to use log4j2 directly with Lombok help
public class LombokLoggingController {
  
	/** NO ESTÁ DANDO ERROR DE COMPILACIÓN POR log NO DEFINIDO PQ INSTALAMOS Lombok.jar EN EL DIRECTORIO DE ECLIPSE.. (ejecutándolo simplemente!)
	 * https://www.baeldung.com/lombok-ide
	 * */
    @RequestMapping("/lombok")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");
  
        return "Howdy! Check out the Logs to see the output...";
    }
}
