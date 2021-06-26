package com.sample.mybatis.configuration;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.zaxxer.hikari.HikariDataSource;

@MapperScan("com.sample.mybatis.model.persistence.mappers_mybatis") //de MyBatis   // You may want to specify a custom annotation or a marker interface for scanning. If so, you must use the @MapperScan annotation NO ES OBLIGATORIA ENTONCES!
@Configuration
public class DatasourceConfiguration {
	
	/* Si queremos que sea en memoria podemos usar este Builder
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			     //.generateUniqueName(true)
			    .setName("myDatabase") 
				.setType(EmbeddedDatabaseType.H2)
			     .setScriptEncoding("UTF-8")
			     .ignoreFailedDrops(true)
			     .addScript("schema.sql")
			     .addScripts("user_data.sql", "country_data.sql")
			     .build();
	}*/

	@Bean(name = {"myDatabase"})
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:~/H2Database/demo/mybatis/mydatabase");//;INIT=runscript from 'classpath:/h2db.sql'");
		dataSource.setDriverClassName("org.h2.Driver");//necesario si usamos la h2 console
		dataSource.setUsername("user");
		dataSource.setPassword("password");
		return dataSource;
	}
	
}
