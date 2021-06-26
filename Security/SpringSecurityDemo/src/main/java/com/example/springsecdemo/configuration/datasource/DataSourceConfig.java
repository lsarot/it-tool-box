package com.example.springsecdemo.configuration.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Primary
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();//de Hibernate
        sessionFactory.setDataSource(dataSourceHibernate());
        sessionFactory.setPackagesToScan("com.example.springsecdemo.model.persistence.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        
        return sessionFactory;
    }
	
    @Bean("hibernate_datasource")
    public DataSource dataSourceHibernate() {    	
    	HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/h2db.sql'");
        dataSource.setUsername("user");
        dataSource.setPassword("pass");
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSourceHibernate());
        em.setPackagesToScan("com.example.springsecdemo.model.persistence.entity");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());//esto se agregó!
        return em;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    @Bean(name = "transactionManager")//NO hizo falta para hacer Tx
    public HibernateTransactionManager hibernateTransactionManager() {//usando @Autowired le puedo pasar SessionFactory sf como parámetro, pero se toma directo del factory.. se vió un ej donde devolvía directamente el HibernateTransactionManager
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
    
}
