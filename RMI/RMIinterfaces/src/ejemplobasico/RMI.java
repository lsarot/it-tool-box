package ejemplobasico;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {
    
    public String metodo(String text) throws RemoteException;
}
