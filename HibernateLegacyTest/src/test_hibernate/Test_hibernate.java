
package test_hibernate;

import Entities.*;
import Entities.controllers.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;

/**
 * USANDO SPRINGBOOT, CAMBIA LA MANERA EN QUE CONFIGURAMOS TODO, YA NO TENDREMOS ARCHIVOS Hibernate.cfg.xml o persistence.xml, sino que application.properties centralizará toda la configuración
 * a menos que queramos no usarla y configuremos aparte hibernate en el proyecto Spring.
*/
public class Test_hibernate {

    @Deprecated
    private int Integer = 0;//prueba de anotación deprecated :) (así colocamos un método deprecated)
    
    /*  PRUEBA DE HIBERNATE
    
    *   trabajamos con hibernate.cfg.xml y también si queremos con persistence.xml de JPA
    *   usamos HibernateUtil con varios ejemplos de inicialización y el JpaUtil si usamos persistence.xml de JPA
    *   el hibernate.reveng sólo sirve para crear entities y mapping files.. pero usamos las que crea JPA (que usamos en tesis)
    *   JPAControllers son usados para evitar hacer todo el código de transactions, liberar recursos y cualquier otro. (los usamos con JpaUtil, entonces no usaríamos el hibernate.cfg)
    
    *   Podemos usar a elección la API de JPA (el JpaUtil, los JpaControllers y el EntityManager) o la API de Hibernate (el HibernateUtil y uso de sessions), 
        pero siempre se requerirá un proveedor de persistencia como Hibernate o EclipseLink, el cual está declarado en el hibernate.cfg o persistence.xml
        por lo que podemos hacer uso i.e. del proveedor Hibernate y de la API de JPA al mismo tiempo.
    */
    
    
    public static void main(String[] args) {        
        // <editor-fold defaultstate="collapsed" desc="Mi código en main">
        //EN LOS 3 ESCENARIOS SE USA HIBERNATE COMO MOTOR DE PERSISTENCIA, PERO DIFERENTES ARCHIVOS DE CONFIGURACIÓN Y DIFERENTES APIs PARA EL CÓDIGO
        //LUEGO CONFIGURACIÓN PROGRAMATICA Y USAMOS POOL C3P0
        //AL FINAL UNA SIMPLE CONEXIÓN JDBC DE REGALO
        
        //1 -------------- USANDO hibernate.cfg y el API Hibernate
        
                /* NOTAS:
                LA CONEXIÓN DEL POOL SE TOMA CUANDO HACEMOS UNA OPERACIÓN SOBRE LA BBDD (ie query.list ... en ese preciso momento debe haber disponible o arroja exception)
                    se libera la conexión al culminar la operación sobre la BBDD
        
                NO SE PUEDE HACER 2 TRANSACTIONS EN SIMULTÁNEO (beginTransaction) (debería usar synchronized blocks si uso transactions ? )
                    USAR transactions NO BLOQUEA, sólo sirve para hacer ROLLBACK si algo sale mal. (debo hacer commit, o rollback)
                    EN EL ARCHIVO Hibernate.php SE ACLARA QUE HIBERNATE TX ES UN BLOQUEO EXCLUSIVO SOBRE LA BBDD, quizás modificando el tx isolation level se pueda solucionar esto.
        
        
                openSession abre sesión y debo cerrarla explícitamente
                getCurrentSession requiere usar transaction, se cierra automáticamente la session.. (configuro en hibernate.cfg, y puedo rescatarla en cualquier lugar)
                
                *** HAY UN PROBLEMA, PQ DEBEMOS USAR TRANSACTIONS Y NO SIRVEN EN SIMULTÁNEO, ASÍ QUE USAMOS UN SYNCHRONIZED, PERO ENTONCES NO SE USA EL POOL DE CONNECTIONS Y TÉCNICAMENTE CON DIF CONNECTIONS SI SE PERMITE VARIAS TRANSACCIONES SOBRE LA BBDD.
                *** SÓLO AL FINAL DEL PROGRAMA HACER SessionFactory.close (sino deja un thread en ejecución), PERO NUNCA ANTES (sino no podemos volver a acceder a la BBDD pq el objeto es static final y tampoco se podrá volver a crear, deberemos iniciar el programa nuevamente).
                */
                
                Runtime.getRuntime().addShutdownHook( new Thread() { // PARA CERRAR SESSION FACTORY AL FINAL DEL PROGRAMA
                    @Override
                    public void run() {
                        System.out.println("Cerrando SessionFactory!..");
                        HibernateUtil.getSessionFactory().close();
                    }
                });
               
                    //-------------------------------
                    //* Iniciamos otro hilo igual, para ver cómo trabajan en simultáneo (el código se optimizó para que funcionara sin errores de concurrencia)
                    Thread thr = new Thread(new Runnable() {
                        @Override
                        public void run() { 
                        	Thread.currentThread().setName("THREAD_SIMULTÁNEO");

                            Session session = HibernateUtil.getSessionFactory().openSession();
                            Transaction tx = null;
                            List<Usuario> usuarios = new ArrayList();
                            List<Parametro> parametros = new ArrayList();
                            try {
                                //USAMOS EL BLOQUE SYNCHRONIZED Y TX PARA MANEJO DE TRANSACCIÓN, PERO LAS TX SE MANEJAN 1 POR 1
                                //synchronized(Test_hibernate.class) {                           
                                //tx = session.beginTransaction();
                                usuarios = session.createQuery("from Usuario u").list(); //si la conn se toma del pool en este momento y usamos un synchronized, no se tomará más de 1 conexión del pool, pero no me permite hacer otra transaction simultánea.
                                parametros = session.createNamedQuery("Parametro.findAll").list();
                                //tx.commit();
                                //HAY UN PROBLEMA PQ, DEBEMOS USAR TRANSACTIONS Y NO SIRVEN EN PARALELO, ASÍ QUE USAMOS UN SYNCHRONIZED, PERO ENTONCES NO SE USA EL POOL DE CONNECTIONS Y TÉCNICAMENTE CON DIF CONNECTIONS SI SE PERMITE VARIAS TRANSACCIONES SOBRE LA BBDD.
                                //}
                            } catch(Exception e) { System.out.println(e); tx.rollback(); 
                            } finally { 
                                session.close(); 
                            }

                            System.out.println();
                            for (Usuario u : usuarios) { System.out.println("Usuario 2: " + u.getNombreApellido()); }
                            for (Parametro p : parametros) { System.out.println("Parametro 2: "+p.getClave()+" - "+p.getValor()); }
                        }
                    });

                    thr.start();
                    //-------------------------------
                
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Transaction tx = null;
        List<Usuario> usuarios = new ArrayList();
        List<Parametro> parametros = new ArrayList();
        try {
            //synchronized(Test_hibernate.class) {
            //tx = session.beginTransaction(); //1 thread a la vez, por eso usamos synchronized para que no falle
            usuarios = session.createQuery("from Usuario u").list(); //debe haber conexiones disponibles en pool
            parametros = session.createNamedQuery("Parametro.findAll").list();
            //tx.commit();
            //}    
        } catch(Exception e) { System.out.println(e); tx.rollback(); 
        } finally { 
            session.close(); 
        }
        
        System.out.println();
        for (Usuario u : usuarios) { System.out.println("Usuario: " + u.getNombreApellido()); }
        for (Parametro p: parametros) { System.out.println("Parametro: "+p.getClave()+" - "+p.getValor()); }
        
        //session.saveOrUpdate(object);*********MEJOR QUE SAVE SÓLO
        //session.delete(object);
        //session.get(Entity.class, id);
        
        
        //2 -------------- USANDO persistence.xml (JPA) y el API de Hibernate
        
        /* debería usarse un código como el de arriba en cuanto a transacción se refiere
        
        System.out.println();
        SessionFactory sessionfactory = (SessionFactory) JpaUtil.getEntityManagerFactory();
        Session s = sessionfactory.openSession();
        
        Usuario u = (Usuario) s.get(Usuario.class, 1);
            System.out.println("Usuario: "+u.getNombreApellido());
        s.close(); sessionfactory.close();
        */
        
        
        
        //3 -------------- USANDO persistence.xml y API JPA con los JpaControllers
        
        UsuarioJpaController uc = new UsuarioJpaController(JpaUtil.getEntityManagerFactory());
        List<Usuario> users = uc.findUsuarioEntities();
        for( Usuario u:users ) { System.out.println("Usuario: "+u.getNombreApellido()); }
        
        
        //4 -------------- USANDO CONFIGURACIÓN PROGRAMÁTICA Y POOL C3P0
        
        testHibernate_configuracionProgramatica_usandoPool();
        
        
        //5 -------------- USANDO SIMPLE CONEXIÓN JDBC SIN ORM, TAMBIÉN MANEJA TRANSACCIONES (PERO CON MUCHAS MÁS COSAS QUE PROBAMOS)
        
        System.out.println();
        Properties connProps = new Properties();
        connProps.put("user", "root");
        connProps.put("password", "root");
        Connection conn = null;
        try {
            //Class.forName("com.mysql.jdbc.Driver"); //antes de JDBC 4.0 era necesario cargar el driver!... (org.apache.derby.jdbc.EmbeddedDriver y .ClientDriver)
            //esto cargaba el bloque static {} del drivermanager
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio_reserva_cubiculo", connProps);
                conn.setAutoCommit(false);
                //conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                conn.setReadOnly(true);
            PreparedStatement ps = conn.prepareStatement("select * from usuario where id_usuario = ?");
                ps.setInt(1, 6);
                
            //** NO USAR AUTOCOMMIT SI QUEREMOS MANEJAR LAS UNIDADES DE TRABAJO MÍNIMA A NUESTRO GUSTO !
            //--- inicia la transacción
            ResultSet rs = ps.executeQuery(); //** AL NO USAR AUTO-COMMIT ES AQUÍ DONDE SE INICIA UNA TRANSACTION, Y DEBO HACER COMMIT O ROLLBACK AUTOMÁTICAMENTE !
            //...puedo hacer otras operaciones sobre la bbdd y al final hago commit
            conn.commit();
            //--- culmina la transacción
            //ps.close(); cerrar al final en finally
            //rs.close();
            
            //--- inicia transacción que dará error y se revertirá lo completado hasta el momento del fallo
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio_reserva_cubiculo", connProps);
                conn.setAutoCommit(false);
            conn.prepareStatement("INSERT INTO `perfil` (`id_perfil`, `rango`) VALUES ('PRUEBA', '4')").execute();
            conn.prepareStatement("INSERT INTO `perfil` (`id_perfil`, `rango`) VALUES ('PRUEBA', '4')").execute(); //este arrojará exception por duplicate entry y se revertirá el anterior también.
            conn.commit();
            //--- culmina transacción
            
            //--- finalmente obtenemos el resultado si era el caso
                ResultSetMetaData metadata = rs.getMetaData();
                int numberOfColumns = metadata.getColumnCount();         
            while(rs.next()) {
                int i=1;
                while(i<numberOfColumns) {
                    System.out.print( rs.getString(i++) + " " );
                } System.out.println();
                //... vas armando los beans y metiéndolos en un List por ejemplo ...
            }
            
            //-------------------- UNAS OPERACIONES ADICIONALES SOBRE LA CONEXIÓN
                //METADATOS DE LA BBDD
                    System.out.println("METADATOS:");
                    DatabaseMetaData dbmd = conn.getMetaData();       
                    String nombreTablas = "%";        // Listamos todas las tablas
                    String tipos[] = { "TABLE" };     // Listamos sólo tablas           
                    ResultSet tablas = dbmd.getTables( null,null,nombreTablas,tipos );
                    while( tablas.next() ) { // Mostramos sólo el nombre de las tablas (guardado en la columna "TABLE_NAME")
                        System.out.println( tablas.getString( tablas.findColumn( "TABLE_NAME" ) ) );
                    }
                    //SI SOPORTA ALGO USAMOS MÉTODOS SUPPORTS
                    System.out.println("Soporta groupsBy: "+dbmd.supportsGroupBy()); 
                    System.out.println("Soporta outerJoins: "+dbmd.supportsOuterJoins()); 
                    System.out.println("Soporta Ansi92entryLevel: "+dbmd.supportsANSI92EntryLevelSQL());
                    System.out.println("Soporta multipleTransactions (in diff connections): "+dbmd.supportsMultipleTransactions());
                    //..... y muchísimos más!
            //--------------------
            
        } catch (SQLException ex) { 
            System.out.println(ex); 
            if (conn != null) {  
                try { 
                    conn.rollback(); conn.close(); 
                } catch (SQLException sqle) {
                    sqle.printStackTrace(); 
                } 
            }
        }
        
        //----------------
        
        System.exit(0);
        // </editor-fold>
    }
    
    
    
    //-------------------------------------------
    public static void testHibernate_configuracionProgramatica_usandoPool() { //AL TRATAR DE USAR EL POOL ARROJA UNA EXCEPTION (SE CONFIGURÓ COMO DICE LA DOCUMENTACIÓN)
        Session session = null;
        Transaction txn = null;
        try {
            //nótese que no usamos bloque synchronized (para probar)
            session = getSessionFactory().openSession();
            txn = session.beginTransaction();
            session.doWork(new Work() {
                @Override
                public void execute(java.sql.Connection conn) throws SQLException {
                    System.out.println("FUNCIONA CON CONFIG PROGRAMÁTICA!");
                    System.out.println("Transaction isolation level is: "+ Environment.isolationLevelToString(conn.getTransactionIsolation()) );
                }
            });
            txn.commit();
        } catch (RuntimeException e) { if ( txn != null && txn.isActive() ) txn.rollback(); throw e;
        } finally { if (session != null) { session.close(); } }
    }
    
    private static SessionFactory getSessionFactory() {
        Properties properties = getProperties();
        Configuration conf = new Configuration().configure().addProperties(properties);
        ServiceRegistry REGISTRY = new StandardServiceRegistryBuilder().applySettings( conf.getProperties() ).build();            
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
            properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            //log settings
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "false");
            //driver settings
            properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/biblio_reserva_cubiculo");
            properties.put("hibernate.connection.username", "root");
            properties.put("hibernate.connection.password", "root");
            //c3p0 settings
            //properties.put("hibernate.c3p0.min_size", 1);
            //properties.put("hibernate.c3p0.max_size", 5);
            //isolation level
            properties.setProperty("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_SERIALIZABLE));
        return properties;
    }
    //-------------------------------------------
}
