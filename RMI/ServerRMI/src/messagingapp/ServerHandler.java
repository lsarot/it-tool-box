package messagingapp;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ejempbasico.RemoteObject;
import java.rmi.NotBoundException;

public class ServerHandler {
    
    //NO PUEDES USAR ESTO COMO UN CHAT, PUESTO QUE NO DEBERÍAS TENER GUARDADO EN MEMORIA MILLONES DE 'INSTANCIAS' DE INTERFACES DE CLIENTES (LAS INTERFACES NO SE INSTANCIAN, ME REFIERO A SUS IMPL)
    //SI SE CAE EL SERVIDOR SE PIERDEN ESAS REFERENCIAS, DEBE HABER UN MECANISMO DE ALMACENAMIENTO EN UNA BBDD
    
    public static void main(String args[]) throws NotBoundException {
        try {
                // SI QUEREMOS QUE RMI SEA CAPAZ DE DESCARGAR CLASES REMOTAS DINAMICAMENTE DEBE EXISTIR UN SECURITY MANAGER
                //if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }
            
            // USAR BIND NO PERMITE CAMBIARLO LUEGO CON REBIND, PERO CON REBIND SI SE PERMITE HACER CAMBIO AL OBJETO ASOCIADO A UN NOMBRE EN EL REGISTRO          
            LocateRegistry.createRegistry(1099);// 1099 es el puerto del registro por defecto, pero puedo usar cualquier otro.
            Naming.rebind("serverRMImessageApp", new ServerRemoteO()); // si el rmiregistry está en este equipo
                //Naming.rebind("rmi://localhost/serverRMImessageApp", new ServerRemoteO()); // si está en otro equipo!
                //Naming.rebind("rmi://localhost:1099/serverRMImessageApp", new ServerRemoteO());
            
                //OTRA FORMA
                //Registry reg = LocateRegistry.createRegistry(1099); // 1099 es el puerto del registro por defecto, pero puedo usar cualquier otro.
                //reg.rebind("serverRMImessageApp", new ServerRemoteO());
            
            System.out.println("Server started - MessageApp!...");         
            do { System.out.print("Type exit to close: ");
                Scanner sc = new Scanner(System.in);
                if(sc.nextLine().equalsIgnoreCase("exit")) {
                    Naming.unbind("serverRMImessageApp");
                    System.exit(1); 
                }
                System.out.println();
            } while(true);
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
