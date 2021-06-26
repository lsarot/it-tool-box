
package remoterefreturn;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cuenta extends Remote {
    
    public TitularCuenta getTitular() throws RemoteException;
    
    public float getSaldo() throws RemoteException;
    
    public float depositar(float valor) throws RemoteException;
}
