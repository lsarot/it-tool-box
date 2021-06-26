package ejempbasico;

import ejemplobasico.RMI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteObject extends UnicastRemoteObject implements RMI {
    
    public RemoteObject() throws RemoteException {
        super();
    }

    @Override
    public String metodo(String text) throws RemoteException {
        System.out.println("Running through remote method!");
        return text + " modificado en método remoto.";
    }
    
}
