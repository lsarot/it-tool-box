package com.example.codigosbasicos.logging.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** LOGBACK LA CREA EL MISMO QUE HIZO LOG4J, SIENDO UNA MEJORA, LUEGO SURGE LOG4J 2.
 * LOGBACK Y LOG4J 1/2 SON LOGGING FRAMEWORKS
 * SLF4J ES UNA FACHADA, UN INTERFACE, y utilizará el framework que encuentre en el pom.xml
 * LOGBACK DEBE UTILIZAR SLF4J, por eso vemos que aquí se emplea Slf4j.
 * */

public class LogbackExample {

    private static final Logger logger = LoggerFactory.getLogger(LogbackExample.class);
    
    //Con SLF4J no podemos setear level en código, por eso usamos la impl para setearlo directamente (cuando usamos Logback pq necesita SLF4J :
			    //ch.qos.logback.classic.Logger parentLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.logback");
			    //parentLogger.setLevel(Level.INFO);
    //Tomar el contexto del ROOT logger
			    //Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
			    //rootLogger.setLevel(Level.ERROR);

    public static void main(String[] args) {
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
    }

}