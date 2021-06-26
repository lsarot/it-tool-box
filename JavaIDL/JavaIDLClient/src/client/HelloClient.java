
package client;

import com.leo.skelstub.HelloApp.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class HelloClient {
    
    public static void main(String args[]) {
        try{
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialHost","localhost");
            props.put("org.omg.CORBA.ORBInitialPort","1050");
            
            // create and initialize the ORB
            ORB orb = ORB.init(args, props);
            
            //RECUPERANDO REF DESDE NAMING SERVICE
            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.  
            NamingContextExt ncRef = NamingContextExtHelper.narrow( objRef );
            // resolve the Object Reference in Naming
            String name = "Hello";
            Hello helloImpl = HelloHelper.narrow( ncRef.resolve_str(name) );

                    //OTRA FORMA DESDE EL NAMING SERVICE   (Resolve the HelloImpl by using INS's corbaname url)
                    //Hello helloImpl = HelloHelper.narrow( orb.string_to_object( "corbaname::localhost:1050#Hello") );
            
                    //RECUPERANDO REF DESDE UN ARCHIVO SINO HAY NAMING SERVICE
                    /*String filename = System.getProperty("user.home") + System.getProperty("file.separator") + "HelloIOR";
                    FileReader fr = new FileReader(filename);
                    BufferedReader br = new BufferedReader(fr);
                    String ior = br.readLine(); br.close();
                    org.omg.CORBA.Object obj = orb.string_to_object( ior );
                    helloImpl = HelloHelper.narrow( obj );*/
                    
            System.out.println("Obtained a handle on server object: " + helloImpl);
            System.out.println( helloImpl.sayHello() );
            helloImpl.shutdown();
            orb.shutdown(true);
            

        } catch (Exception e) { System.out.println("ERROR : " + e); e.printStackTrace(System.out); }
    }

}