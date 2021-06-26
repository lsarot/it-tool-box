package messagingapp;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import messageapp.ServerRmi;

public class ClientHandler {
    
    public static void main(String args[]) {
        try {
                // SI QUEREMOS QUE RMI SEA CAPAZ DE DESCARGAR CLASES REMOTAS DINAMICAMENTE DEBE EXISTIR UN SECURITY MANAGER
                //if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }
            
            //ServerRmi rmi = (ServerRmi) Naming.lookup("rmi://localhost:1099/serverRMImessageApp");
            //ServerRmi rmi = (ServerRmi) Naming.lookup("//localhost:1099/serverRMImessageApp");
            
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);// "127.0.0.1" "localhost"
            ServerRmi rmi = (ServerRmi) reg.lookup("serverRMImessageApp");//ServerRmi es la interfaz remota del servidor
            
                //for(String s : reg.list()) { System.out.println(s); }//muestra lista de servicios en registro!
            
            //GENERAMOS UN ID ALEATORIO AL CLIENTE, PARA USO DE MI APP, NADA DE RMI
            String userID = "0424-";
            for (int i=0; i<7; i++)
                userID += (int) (Math.random() * 10);
            
            new ClientRemoteO(userID, rmi);
            
        } catch (NotBoundException | RemoteException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }// catch (MalformedURLException ex) {
           // Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        //}
    }
    
}
