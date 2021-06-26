
package maquinaremota;

import computeengine.Compute;
import computeengine.Task;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine extends UnicastRemoteObject implements Compute {
    
    //EXTIENDE DE UNICASTREMOTEOBJECT, PERO NO ES NECESARIO, REVISAR EXPLICACIÓN DE USO EN ServerHandler

    public ComputeEngine() throws RemoteException {
        super();//se colocó por formalidad
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        
        /*
        ComputeEngine implementa interface Compute (es remota), su método executeTask recibe una interface Task (no remota) como parámetro y ejecuta su método execute y retorna resultado.
        Interface Task, está en el servidor y el cliente pq ambos la usan (se guarda en ambos para que compile), pero la implementación está en el cliente (MiTarea), clase que se descargará dinámicamente,
        entonces hace falta configurar un security manager, pero en este ejemplo no lo logré así que se guarda localmente para que funcione.
        
        Recordar que se pueden recibir (parámetros) y retornar (return) cualquier objeto de tipo primitivo, serializable o remoto.
            Primitivo y serializable se pasan por copia, remoto por referencia.
                Serializable, se pasan atributos siempre que NO sean static o transient.
                Remoto, se acceden a métodos de las interfaces remotas que implemente, ya que pudiera tener métodos para uso local dicho objeto, a los que no puede accederse con la ref remota (proxy).
        */
        
        return t.execute();
    }
}