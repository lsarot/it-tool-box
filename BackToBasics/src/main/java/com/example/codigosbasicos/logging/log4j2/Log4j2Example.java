package com.example.codigosbasicos.logging.log4j2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.cassandra.CassandraAppender;
import org.apache.logging.log4j.cassandra.CassandraManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.example.codigosbasicos.logging.log4j2.cassandra_custom_appender.CassandraAppenderCustom;
import com.example.codigosbasicos.logging.log4j2.cassandra_custom_appender.CassandraManagerCustom;

import java.util.concurrent.locks.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**Logging is a powerful aid for understanding and debugging program's run-time behavior. Logs capture and persist the important data and make it available for analysis at any point in time.
 * This article discusses the most popular java logging frameworks, Log4j 2 and Logback, along with their predecessor Log4j, and briefly touches upon SLF4J, a logging facade that provides a common interface for different logging frameworks.
 * */

/** Configuring Log4j 2 is based on the main configuration log4j2.xml file. The first thing to configure is the appender.
 * These determine where the log message will be routed. Destination can be a console, a file, socket, etc.
 * Log4j 2 has many appenders for different purposes, you can find more information on the official Log4j 2 site. ( https://logging.apache.org/log4j/2.x/manual/appenders.html )
 * */

/** LEVELS
 * A log request of level p in a logger with level q is enabled if p >= q. This rule is at the heart of log4j. It assumes that levels are ordered. For the standard levels, we have 
 * ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
 * ie. If we set WARN level on code or on log4j2.xml file, then we will get output for WARN, ERROR, FATAL logging... and OFF, we will get NO output.
 * Programmatic setting will override xml setting !
 * */

/** LOCATION
 * Extracting location is an expensive operation (it can make logging 5-20 times slower on synchronous and 30-100 times slower on asynchronous)
 * %l %c %F %M %L or %location %class %file %method %line are all expensive tasks, cause it has to search on the stacktrace
 * Por eso se debe hardcodear el method name en el mensaje. El parámetro %c o %logger nos dá el nombre del logger que usualmente será el nombre de la clase.
 * */

public class Log4j2Example {
	
	private static Logger logger ;//= LogManager.getLogger(Log4j2Example.class); //lo seteamos luego, por si queremos que tome cambios en la configuration
    //private static org.slf4j.Logger logger = LoggerFactory.getLogger(Log4j2Example.class);   //TO USE SLF4J INSTEAD!
    
    public static void main(String[] args) {
    	//LOG TO MONGODB
    	//org.apache.logging.log4j.core.appender.nosql.NoSqlAppender     es la clase del appender
    	//org.apache.logging.log4j.mongodb3.MongoDbProvider
    	
    	//LOG TO CASSANDRA
    	//NO HABÍA MANERA DE SETEAR CONSISTENCY LEVEL A LOCAL_QUORUM (PARA AWS KEYSPACES)
    	//AWS KEYSPACES NO PERMITE MUCHAS CQL CLAUSE, Y YA DE POR SÍ CQL ES MÁS LIMITADO QUE SLQ
    	//PROBAREMOS CON MONGODB
		    	/* WORKAROUNDS:
		    	 * Creamos un custom log4j2 appender, CassandraAppenderCustom.class y las clases necesarias (copias de las originales)
		    	 * En el Manager, se modificaron unas líneas para customizar el consistency level y se intentó usar TTL, pero AWS Keyspaces no lo permite todavía.
		    	 * */
				    	//aquí un intento de seteo global (no por query) y también se hizo en application.conf file, e incluso se modificó el fichero de la librería jar reference.conf (esto para el driver 4.7.2) que forzamos su uso dándole prioridad en el classpath
						    	//System.setProperty("datastax-java-driver.basic.request.consistency", "LOCAL_QUORUM");
						    	//System.setProperty("datastax-java-driver.basic.consistency", "LOCAL_QUORUM");
						    	//y con: -Ddatastax-java-driver.basic.request.consistency="LOCAL_QUORUM"
						    	
								    	//for com.datastax.cassandra:cassandra-driver-core, tampoco funcionó!
						    			//QueryOptions qo = new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
						    			//qo.setSerialConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
								    	//com.datastax.driver.core.querybuilder.QueryBuilder.insertInto("").setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
								    	//com.datastax.driver.core.Configuration.builder().withQueryOptions(qo).build();
    	//---------------------------------------
    	
    	//To make all loggers asynchronous, don't forget to set system property
    	//-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -->
    	//System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    	//or configure it on xml using <AsyncLogger> or <AsyncRoot>
    	
		
    	//::: SET LEVEL PROGRAMMATICALLY ::: (will override xml setting)
    	//setLevelProgrammatically(Level.WARN);
    	
    	logger = LogManager.getLogger(Log4j2Example.class);    	
    	
    	System.out.println("Minimum Level: " + logger.getLevel().name());
    	//---------------------------------------
    	logger.traceEntry(); //notar que es nivel trace

    	logger.trace("{} {}!", "Trace", "Message"); //we can set message params
        logger.debug("Debug Message!");
        logger.info("Info Message!");
        logger.warn("Warn Message!");
        logger.error("Error Message!");  //logger.error("Error log message", throwable);
        logger.fatal("Fatal Message!");
	        	//for Slf4j Fatal level ?? no (lo marca como ERROR)!  -> The FATAL logging level got dropped in Slf4j based on the premise that in a logging framework, we should not decide when an application should be terminated.
	        	//Marker fatal = MarkerFactory.getMarker("FATAL");
	        	//logger.error(fatal, "Fatal Message!");
        
        anotherMethod();
        logger.traceExit();
        System.exit(0);
    }
    
    private static void setLevelProgrammatically(Level level) {
		// org.apache.logging.log4j.core.config.Configurator;
		Configurator.setLevel("com.example.codigosbasicos.logging.log4j2.Log4j2Example", level); //esta se impone sobre la rootLevel para este package o class
		// You can also set the root logger:
		Configurator.setRootLevel(level);
		
		//CON MÁS DETALLES DE LA CONFIGURACIÓN
		LoggerContext ctx = (LoggerContext) LogManager.getContext(true);//false tenía
		Configuration config = ctx.getConfiguration();		
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(Level.WARN);
		//AbstractDatabaseAppender<CassandraManager> appender = config.getAppender("Cassandra");
		CassandraAppenderCustom appender = config.getAppender("Cassandra");
		CassandraManagerCustom man = appender.getManager();
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
			// and if you want to change for just a particular logger (of a class/ package) get the context of that logger, setLevel and updateLoggers
		
    }
    
    private static void anotherMethod() {
    	logger.info("[anotherMethod] Message on another method!");
    	try {
    	    throw new Exception("myCustomExceptionMessage");
    	} catch (Exception e) {
    	    //logger.error("{}. Error log message", "Hello", e);
    		
    		//MONGO DB DÁ TAREA CON POJOS QUE NO ENTIENDE EL TRANSFORMADOR
    		//org.apache.commons:commons-lang3
    		//String stacktrace = ExceptionUtils.getStackTrace(e);
    		//Core Java (<9)
    		//StringWriter sw = new StringWriter();   PrintWriter pw = new PrintWriter(sw);   e.printStackTrace(pw);   sw.toString()
    		//Core Java (>9)
    		// https://www.baeldung.com/java-9-stackwalking-api
    	    logger.error("{}. Error log message.\n{}", "Hello", StringUtils.substring(ExceptionUtils.getStackTrace(e), 0, 500));
    	    //e.printStackTrace();
    	}
    }
    
}
