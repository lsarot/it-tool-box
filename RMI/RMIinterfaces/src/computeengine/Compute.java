
package computeengine;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
    
    //SOBRE EL <T> T:
        //TASK INTERFACE RECIBE T (así: Task<T>), por eso pueden sus métodos devolver T directamente y se entiende que es del tipo que recibe
        //COMPUTE INTERFACE no recibe T (no es Compute<T>), entonces se declara el retorno de su método de esta manera <T> T
    
    public <T> T executeTask(Task<T> t) throws RemoteException;
    
}