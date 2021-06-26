package com.example.springwebdemo.configuration.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * AQUÍ NO USAMOS SPRING PARA NADA!, NI HIBERNATE EN NINGÚN DATASOURCE
 * 
 * https://www.baeldung.com/java-in-memory-databases
 * */
public class DBConfig_1_Source {

	private DBConfig_1_Source() {}

	//hibernate.dialect=org.hibernate.dialect.H2Dialect pudieramos setearlo!
	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;

	static {
		config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");
		config.setUsername("database_username");
		config.setPassword("database_password");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		ds = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	public static DataSource getDataSource() {
		return ds;
	}
	   
}
