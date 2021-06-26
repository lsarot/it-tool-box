package messagingapp;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import messageapp.ClientRmi;
import messageapp.ServerRmi;

public class ClientRemoteO extends UnicastRemoteObject implements ClientRmi {

    private final ServerRmi serverRmi;
    private final String clientName;
    
    public ClientRemoteO(String clientName, ServerRmi serverRmi) throws RemoteException {
        super();
        this.clientName = clientName;
        this.serverRmi = serverRmi;
        serverRmi.registerClient(clientName, this);
        this.chat();
    }
    
    @Override
    public void showMessage(String message) throws RemoteException {
        System.out.println("> " + message);
    }
    
    @Override    // NO UTILIZADO!
    public String getClientName() throws RemoteException {
        return clientName;
    }
    
    public void chat() throws RemoteException {
        System.out.println("Ejecutando ClientHandler!\n   Type exit to close the chat.\n");
        String text = "";
        while(!text.equalsIgnoreCase("exit")) {      
            Scanner sc = new Scanner(System.in);
            text = sc.nextLine();
            
            if(text.equalsIgnoreCase("exit"))
                serverRmi.deregisterClient(clientName, this);
            else 
                serverRmi.broadcastMessage(clientName + ": " + text);
        }
        System.exit(1);
    }
}
