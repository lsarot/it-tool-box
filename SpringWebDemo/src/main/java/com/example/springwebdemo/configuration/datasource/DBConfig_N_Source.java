package com.example.springwebdemo.configuration.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySources(value = {
		//@PropertySource("file:src/main/resources/h2_1.properties"),
		//@PropertySource("file:src/main/resources/h2_2.properties")
				//@PropertySource("classpath:h2_1.properties") //"classpath:mysql.properties"
				//@PropertySource("classpath:h2_2.properties")
				//@PropertySource("classpath:${db2.name}.properties") //con un placeholder, y se usaría la variable   ie. db2.name=postgresql   encontrada en los ficheros cargados previamente!
				//@PropertySource("file:${MY_PATH}/application.properties") //system property or env variable, or previously found property?
				//@PropertySource("file:${ws.properties}") //-Dws.properties=file:/path-to.properties
				//@PropertySource("${external.app.properties.file}", ignoreResourceNotFound = true)
})
public class DBConfig_N_Source {

	/* SE PUEDE RECUPERAR .properties SOBRE VARIABLES EN LA CLASE
	@Value("${jdbc.url}")
	private String jdbcUrl;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    */
	
    
	@Autowired private Environment env;   
    
	
	/**
	 * OTRA FORMA DE DECLARAR LA EMBEDDED DDBB QUE TRAE SPRING, HSQL, H2 O DERBI
	 * notar que usamos EmbeddedDatabaseBuilder
	 * */
	//@Bean
	public DataSource dataSource() {
	    return new EmbeddedDatabaseBuilder()
	        .setType(EmbeddedDatabaseType.DERBY)
	        .setName("databaseName")
	        .setScriptEncoding("UTF-8")
	        //.ignoreFailedDrops(true)
	        .addScript("classpath:org/springframework/security/core/userdetails/jdbc/users.ddl")
	        //.addScript("schema.sql")
	        //.addScripts("user_data.sql", "country_data.sql")
	        .build();
	}
	
	/**
	 * SETEAMOS UNA EXTRA COMO PRIMARY, PARA PROBAR H2 CONSOLE, Y GUARDAR CUENTAS DE ACCESO EN ESA BBDD
	 * notar que usamos HikariDataSource, es lo que usaríamos en producción (claro sin una in-memory db, aunque esta no está configurada in-memory, pero H2 no es para algo pesado)
	 * */
	@Primary
	@Bean(name = {"credentials-store-default"})
	public DataSource configureH2DataSource_CredentialsStoreSpringSecSchema() {
		HikariDataSource dataSource = new HikariDataSource();
		//la va a guardar en /Users/Leo/H2Database/demo/credentials/default
		dataSource.setJdbcUrl("jdbc:h2:~/H2Database/demo/credentials/default");//;INIT=runscript from 'classpath:/h2db.sql'");
		dataSource.setDriverClassName("org.h2.Driver");//necesario si usamos la h2 console
		dataSource.setUsername("user");
		dataSource.setPassword("pass");
		return dataSource;
	}
	
	@Bean(name = {"credentials-store-own"})
	public DataSource configureH2DataSource_CredentialsStoreOwnSchema() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:~/H2Database/demo/credentials/ownschema;INIT=runscript from 'classpath:/my_own_credentials_schema.sql'");
		dataSource.setDriverClassName("org.h2.Driver");//necesario si usamos la h2 console
		dataSource.setUsername("user");
		dataSource.setPassword("pass");
		return dataSource;
	}
	
   @Bean("h2_1")
   public DataSource configureH2DataSource_1() {
      HikariDataSource dataSource = new HikariDataSource();
      //dataSource.setDriverClassName(env.getProperty("db.driver")); //para drivers viejos, ya con el url determina el driver a cargar por el schema
      //TOMAR PROPERTIES DE .properties ME ESTABA DANDO FALLO AL BUSCAR EL FICHERO SI DESPLEGABA EN UN TOMCAT EXTERNO
      //jdbc.url=jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'
	  //jdbc.username=user
	  //jdbc.password=pass
      dataSource.setJdbcUrl("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");//(env.getProperty("jdbc.url"));
      dataSource.setUsername("user");//(env.getProperty("jdbc.username"));
      dataSource.setPassword("pass");//(env.getProperty("jdbc.password"));
      return dataSource;
   }
   
   @Bean(name = {"h2_2"}) //("postgresql")
   //@Scope("singleton") remember is by default
   public DataSource configureH2DataSource_2() {
      HikariDataSource dataSource = new HikariDataSource();
      dataSource.setJdbcUrl("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");//(env.getProperty("jdbc.url"));
      dataSource.setUsername("user");//(env.getProperty("jdbc.username"));
      dataSource.setPassword("pass");//(env.getProperty("jdbc.password"));
      return dataSource;
   }
   
   //--------------------------------------------
   
   /*HSQLDB parece que tiene una tema a la hora de arrancar desde un script, como que hay que crear la bbdd y guardarla en un fichero y luego abrirla en modo read-only
   @Bean("hsqldb-ds")
   public DataSource configureHSQLdbDataSource() {
	  HikariDataSource dataSource = new HikariDataSource();
      dataSource.setJdbcUrl("jdbc:hsqldb:mem:myDb");
      dataSource.setUsername("user");
      dataSource.setPassword("pass");
      return dataSource;
   }*/
   
   /*DERBY parece que tiene otra complejidad para crear la bbdd a partir de un script .sql
   @Bean("derby-ds")
   public DataSource configureDerbyDataSource() {
	  HikariDataSource dataSource = new HikariDataSource();
      dataSource.setJdbcUrl("jdbc:derby:memory:myDb;create=true");
      dataSource.setUsername("user");
      dataSource.setPassword("pass");
      return dataSource;
   }*/
   
   /* Y SQLite tiene otro problema:
    HIBERNATE DIALECT NOT PRESENT FOR SQLite

	Since SQLite is an embedded database for C-like environments, written in C and thus compiled to native code, 
	changes that Hibernate (or any ORM) will support aren't really high. Java is cross-platform and it would be a
	bit weird to have a platform-dependent dependency. On Android, SQLite is used, but there the platform supplies a JDBC driver for it.
	
	Usually, Windows binaries are compatible over different Windows versions - as long as the architecture stays the same. 
	If you look at the SQLite download page you'll notice there's a 32-bit pre-built Windows binary. This one can be used 
	on almost any Windows version (except Windows RT, maybe), but you cannot use it on Linux or OS X. In order to use SQLite 
	from Java, you would need to include the correct binary for the specific OS / architecture, effectively making a Java 
	application platform-dependent. That is something you usually don't want.
	
	If you're building a desktop application in Swing and you want to use an embedded database, my suggestion would be to use 
	a Java embedded database, like H2, HSQL or Derby. The latter is also shipped with Oracle Java as JavaDB. All are supported 
	as hibernate dialects.
	
	There may be other factors you want to consider, for example the fact that SQLite is written in native code and hence may have
	better performance. In the end, the only one who can decide which database is best is the one who is building the system.
    */
   
}
