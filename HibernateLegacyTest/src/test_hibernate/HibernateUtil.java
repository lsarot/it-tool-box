
package test_hibernate;

import java.sql.Connection;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

//COLOCAMOS LAS 3 OPCIONES PARA CREAR EL SESSION FACTORY DEL .cfg, SÓLO DEBO DESCOMENTAR LA QUE QUIERA USAR

public final class HibernateUtil {
    
    private HibernateUtil(){}
    
    public static SessionFactory getSessionFactory() { return sessionFactory; }
    
    //----------
    
    /* NO TIENE QUE SER UN SINGLETON PQ NO USAMOS ATRIBUTOS O MÉTODOS DE INSTANCIA.
    
    private static HibernateUtil INSTANCE = null;
    
    public static HibernateUtil getInstance() {    
        if (INSTANCE == null) {
            synchronized(HibernateUtil.class) {
                if (INSTANCE == null) { INSTANCE = new HibernateUtil(); }
            }
        }
        return INSTANCE;
    }
    
    
    @Override
    public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
    */
    
    //----------
    
    
    private static final SessionFactory sessionFactory;
    
    static {
        try { // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    
    //----------
    
     
    //IGUAL AL DE ARRIBA PERO LE ENVIAMOS PROPERTIES
    /*
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static ServiceRegistry REGISTRY;

    private static SessionFactory buildSessionFactory() {
        Properties properties = getProperties();
        
        Configuration conf = new Configuration();
        try {
            conf.configure().addProperties(properties);
        } catch(HibernateException e) {
            conf.configure("/META-INF/hibernate.cfg.xml");
        }
        
        REGISTRY = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
        
        try {
            return conf.buildSessionFactory( REGISTRY );
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy( REGISTRY );
        }
        return null;
        
    }
    
            private static Properties getProperties() {
                Properties properties = new Properties();
                    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                    //log settings
                    properties.put("hibernate.hbm2ddl.auto", "update");
                    properties.put("hibernate.show_sql", "false");
                    //driver settings
                    properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
                    properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/biblio_reserva_cubiculo");
                    properties.put("hibernate.connection.username", "root");
                    properties.put("hibernate.connection.password", "root");
                    //c3p0 settings
                    properties.put("hibernate.c3p0.min_size", 1);
                    properties.put("hibernate.c3p0.max_size", 5);
                    //isolation level
                    properties.setProperty("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_SERIALIZABLE));
                return properties;
            }
    */
    
    //----------
    
    /*
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            return new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
        return null;
    }
    */
    
}