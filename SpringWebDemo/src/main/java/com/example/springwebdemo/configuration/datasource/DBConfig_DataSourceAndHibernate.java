package com.example.springwebdemo.configuration.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
//@EnableTransactionManagement .. leer EmployeeRepo.java sobre su uso
public class DBConfig_DataSourceAndHibernate {

	/**
    * SPRING SESSION FACTORY BEAN RESPONSIBLE OF CREATING HIBERNATE SESSION FACTORY
    * 
    * SI QUIERO USAR 2 BBDD Y USAR HIBERNATE EN AMBAS, USO PRIMARY EN LA PRINCIPAL Y LA OTRA LA REFERENCIO POR NOMBRE E INYECTO EN LOS REPOSITORY TIPO CLASE, YA QUE LOS TIPO INTERFACE USARÁN EL PRIMARY
    */
    //@Primary
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();//de Hibernate
        sessionFactory.setDataSource(dataSourceHibernate());
        sessionFactory.setPackagesToScan("com.example.springwebdemo.model.persistence.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        
        return sessionFactory;
    }
	
    @Bean("hibernate_datasource") //NO hace falta que sea Bean, pero no estaría de más tener la ref en el contexto del contenedor
    public DataSource dataSourceHibernate() {    	
    	HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");//(env.getProperty("jdbc.url"));
        dataSource.setUsername("user");//(env.getProperty("jdbc.username"));
        dataSource.setPassword("pass");//(env.getProperty("jdbc.password"));
        
        return dataSource;
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hibernateProperties.setProperty("hibernate.ddl-auto", "none");
        hibernateProperties.setProperty("jpa.generate-ddl","false");

        hibernateProperties.setProperty("logging.level.org.hibernate.SQL", "DEBUG");
        hibernateProperties.setProperty("logging.level.org.hibernate.type", "TRACE");

        return hibernateProperties;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    @Bean(name = "transactionManager")//NO hizo falta para hacer Tx
    public HibernateTransactionManager hibernateTransactionManager() {
    	//usando @Autowired le puedo pasar SessionFactory sf como parámetro, pero se toma directo del método.. se vió un ej donde devolvía directamente el HibernateTransactionManager, sin hacer .setSessionFactory
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
    
}
