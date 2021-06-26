package messageapp;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRmi extends Remote {
    
    public void registerClient(String clientName, ClientRmi clientRmi) throws RemoteException;
    
    public void deregisterClient(String clientName, ClientRmi clientRmi) throws RemoteException;
    
    public void broadcastMessage(String message) throws RemoteException;
}
