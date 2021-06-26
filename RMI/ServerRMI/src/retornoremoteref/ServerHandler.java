
package retornoremoteref;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler {

    public static void main(String args[]) {
        try {
            Registry reg = LocateRegistry.createRegistry(1099);// 1099 es el puerto del registro por defecto, pero puedo usar cualquier otro.
            reg.rebind("BankService", new BankServiceImpl());
            
            System.out.println("Server started - BankService! ... Type exit to close.");
            Scanner sc = new Scanner(System.in);
            if(sc.nextLine().equalsIgnoreCase("exit")) { System.exit(1); }
            
        } catch (RemoteException ex) {
            Logger.getLogger(messagingapp.ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
