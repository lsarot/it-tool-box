package messageapp;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRmi extends Remote {
    
    public void showMessage(String message) throws RemoteException;
    
    public String getClientName() throws RemoteException;
}
