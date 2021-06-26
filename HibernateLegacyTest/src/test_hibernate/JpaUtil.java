
package test_hibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil{
    
    private static final EntityManagerFactory emf;
    
    static{
       try{   
            emf = Persistence.createEntityManagerFactory("Test_hibernatePU");       
       }catch(Throwable t){
            System.out.println("Error al inicializar el EMF "+t);
            throw new ExceptionInInitializerError();
       }       
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){ return emf; }   
}