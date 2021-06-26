
package remoterefreturn;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BankServiceInterf extends Remote {
    
    public Cuenta crearCuenta(TitularCuenta t) throws RemoteException;
    
    public List<Cuenta> getCuentas() throws RemoteException;
}