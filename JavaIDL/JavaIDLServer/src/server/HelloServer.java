
package server;

import com.leo.skelstub.HelloApp.*;
import java.io.*;
import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;

public class HelloServer {

    public static void main(String[] args) {
        try{
            arrancarNamingService();
            
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialHost","localhost");
            props.put("org.omg.CORBA.ORBInitialPort","1050");
            
            // create and initialize the ORB
            ORB orb = ORB.init(args, props);
            
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow( orb.resolve_initial_references("RootPOA") ); //o POA rootpoa = (POA) orb.resolve_initial_references( "RootPOA" );
            rootpoa.the_POAManager().activate(); //QUITAR SI USO PERSISTENT POA SERVER Y NO TRANSIENT
            
            // create servant and register it with the ORB
            HelloImpl helloImpl = new HelloImpl();
            helloImpl.setORB( orb ); //esto no es obligatorio, le pasamos una ref por si manejase su uso, en shutdown lo usa por ej
            
                //POA SERVER ONLY
                // get object reference from the servant
                org.omg.CORBA.Object ref = rootpoa.servant_to_reference( helloImpl );
                Hello helloRef = HelloHelper.narrow( ref );

                //TIE SERVER ONLY    (recordar usar en el compilador idlj -fallTIE Hello.idl)
                /*// create a tie, with servant being the delegate.
                HelloPOATie tie = new HelloPOATie(helloImpl, rootpoa);
                // obtain the objectRef for the tie, this step also implicitly activates the object
                Hello helloRef = tie._this( orb );*/

                
                //------ POA CON PERSISTENT POLICY (persistent server, el otro es transient) (debo registrar con el servertool)
                //ORB orb...
                //POA rootpoa...
                //HelloImpl helloImpl...
                //helloImpl...
                /*org.omg.CORBA.Policy[] persistentPolicy = new org.omg.CORBA.Policy[1];
                persistentPolicy[0] = rootpoa.create_lifespan_policy( LifespanPolicyValue.PERSISTENT );
                POA persistentPOA = rootpoa.create_POA("childPOA", null, persistentPolicy);
                persistentPOA.the_POAManager().activate( );
                persistentPOA.activate_object( helloImpl );
                org.omg.CORBA.Object ref = persistentPOA.servant_to_reference( helloImpl );
                Hello helloRef = HelloHelper.narrow( ref );*/
                //.. el resto como sigue...
                //------
                
                
            //GUARDANDO REF EN NAMING SERVICE
            // get the root naming context.. "NameService" invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow( objRef );
            // bind the Object Reference in Naming
            String name = "Hello";
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, helloRef);
                
                    //GUARDANDO REF EN ARCHIVO SINO HAY NAMING SERVICE DISPONIBLE   (SIRVE PARA CUALQUIER IMPLEMENTACIÓN DE ORBS) (el string se puede guardar en un fichero compartido en red, en una BBDD, un objeto JNDI, etc.) ... i.e. InputStream is = new URL("http://miurl.com/file").openStream();
                    /*String ior = orb.object_to_string( helloRef );
                    String filename = System.getProperty("user.home") + System.getProperty("file.separator") + "HelloIOR";
                    System.out.println("IORpath: "+filename);
                    FileOutputStream fos = new FileOutputStream( filename );
                    PrintStream ps = new PrintStream( fos );
                    ps.print( ior );   ps.close();*/
                    
            System.out.println("HelloServer ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        } catch (Exception e) { System.err.println("ERROR: " + e); e.printStackTrace(System.out); }
        System.out.println("HelloServer Exiting ...");
        pararNamingService();
    }

    private static void arrancarNamingService() {
        try {
            Runtime.getRuntime().exec("orbd -ORBInitialPort 1050");
            
        } catch(RuntimeException | IOException e){ System.out.println(e); }
    }

    private static void pararNamingService() {
        try {
            Runtime.getRuntime().exec("pkill orbd");//en UNIX based OS, Windows usará otro
            
        } catch(RuntimeException | IOException e){ System.out.println(e); }
    }

}

//**TRABAJANDO CON EL NAMING SERVICE USANDO SUBCONTEXTOS
        /*
        //obtenemos el contexto de nombres inicial
        NamingContext ncRef = orb.string_to_object("aquí el stringified del naming context que entrega por ej tnameserv(naming service anterior a orbd)"); //si está stringified
            NamingContextExt ncRef = NamingContextExtHelper.narrow( orb.resolve_initial_references("NameService") );//si no está stringified
        
        //creamos, enlazamos el subcontext y le damos un nombre
        NamingContext simpleCxt = ncRef.new_context(); //nuevo subcontext
        NameComponent simpleName[] = { new NameComponent("misimple", ""), new NameComponent("subcontexto","") };
        ncRef.bind_context(simpleName, simpleCxt);    NameService->misimple->subcontexto
        
        //lo recuperamos y transformamos (por ej si ya estaba bounded)
        org.omg.CORBA.Object objRef = ncRef.resolve( simpleName ); 
        simpleCxt = NamingContextHelper.narrow( objRef ); //lo transformamos
        
        //exportamos el objeto al nuevo subcontext
        NameComponent objName[] = { new NameComponent("object", "") };
        simpleCxt.rebind( objName, org.omg.CORBA.Object por ej Hello );  NameService-> misimple-> subcontexto-> object
        */

