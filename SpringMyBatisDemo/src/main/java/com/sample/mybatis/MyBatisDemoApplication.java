package com.sample.mybatis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** MyBatis es un fork de iBatis3.0

A diferencia de las herramientas ORM
		MyBatis no mapea objetos Java a tablas de base de datos sino métodos a sentencias SQL.

-Permite utilizar todas las funcionalidades de la base de datos como procedimientos almacenados, vistas, consultas de cualquier complejidad o funcionalidades específicas del proveedor. Es una herramienta indicada para bases de datos legadas, desnormalizadas o cuando es preciso tener el control total del SQL ejecutado.
-Simplifica la programación frente al uso directo de JDBC. Las líneas de código necesarias para ejecutar una sentencia se reducen casi siempre a una. Esta simplificación ahorra tiempo y evita errores habituales como olvidar cerrar una conexión a base de datos, realizar incorrectamente un mapeo de datos, exceder el tamaño de un result set u obtener varios resultados cuando se esperaba solo uno.
-Proporciona un motor de mapeo de resultados SQL a árboles de objetos basado en información declarativa.
-Soporta la composición de sentencias SQL dinámicas mediante un lenguaje con sintaxis tipo XML.
-Soporta integración con Spring Framework y Google Guice. Esta característica, permite construir código de negocio libre de dependencias, incluso sin llamadas al API de MyBatis. 
 
 * VER EL FICHERO    Hibernate Vs MyBatis.txt
 * 
 * 
 *   PROBAR CON ENDPOINTS QUE FUNCIONAN:
 *   /api/v1/address/
 *   /api/v1/contact/
 *   
 *   los otros no se revisaron ni se crean las tablas en el schema !!!
 *   
 *   
 *   SWAGGER:
 *   http://localhost:8080/swagger-ui.html
 *   http://localhost:8080/v2/api-docs?group=api-group-name(com.leo)
 *   http://localhost:8080/swagger-resources
 *   http://localhost:8080/swagger-resources/configuration/security
 *   http://localhost:8080/swagger-resources/configuration/ui
 * */

@SpringBootApplication
public class MyBatisDemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MyBatisDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}
}
