package messagingapp;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import messageapp.ClientRmi;
import messageapp.ServerRmi;

public class ServerRemoteO extends UnicastRemoteObject implements ServerRmi {
    
    private ArrayList<ClientRmi> clientList;
    
    /* SI LA CLASE NO PUEDE EXTENDER DE UnicastRemoteObject, POR EJEMPLO SI EXTIENDE DE APPLET.. EN EL CONSTRUCTOR LO EXPORTAMOS AL REGISTRY
    public ServerRemoteO() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 1099);
    }
    */
    
    public ServerRemoteO() throws RemoteException {
        super();
        clientList = new ArrayList<>();
    }

    @Override
    public void registerClient(String clientName, ClientRmi clientRmi) throws RemoteException {
        clientList.add(clientRmi);
        broadcastMessage(clientName + " has joined the chat!");
    }

    @Override
    public void deregisterClient(String clientName, ClientRmi clientRmi) throws RemoteException {
        clientList.remove(clientList.indexOf(clientRmi));
        broadcastMessage(clientName + " has abandoned the chat!");
    }
    
    @Override
    public void broadcastMessage(String message) throws RemoteException {
        for(ClientRmi client : clientList) {
            client.showMessage(message);
        }
    }
}

// EN EL LADO CLIENTE SE OBTIENE LA PRIMERA REFERENCIA REMOTA(REF A UN OBJETO QUE IMPLEMENTA LA INTERFAZ REMOTE) A TRAVÉS DEL RMIREGISTRY
//..LUEGO CLIENTE Y SERVIDOR SE PUEDEN PASAR REFERENCIAS REMOTAS EN AMBOS SENTIDOS.

// SI SE PASA UNA REF REMOTA COMO PARÁMETRO, ESTA SE PASA POR REFERENCIA PROPIAMENTE Y NO POR VALOR COMO SE COMENTÓ ANTES POR EL TEMA DE QUE CORRESPONDEN
//..A DIF. ESPACIOS DE DIRECCIONES POR SER DIF. JVM.

