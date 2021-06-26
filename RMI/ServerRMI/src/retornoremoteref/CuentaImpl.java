
package retornoremoteref;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import remoterefreturn.Cuenta;
import remoterefreturn.TitularCuenta;

public class CuentaImpl extends UnicastRemoteObject implements Cuenta, Unreferenced {
    
    private TitularCuenta titular; 
    private float saldo = 0;
    
    public CuentaImpl(TitularCuenta t) throws RemoteException {
        super();
        titular = t;
    }
    
    @Override
    public TitularCuenta getTitular() throws RemoteException {
        return titular;
    }
    
    @Override
    public float getSaldo() throws RemoteException {
        return saldo;
    }

    @Override
    public float depositar(float valor) throws RemoteException {
        saldo += valor;
        return saldo;
    }

    //APARENTEMENTE SE LLAMA UN TIEMPO DESPUÉS DE QUE EL RUNTIME DETERMINE QUE YA NO HAY CLIENTES CON LA REFERENCIA A ESTA INSTANCIA
    @Override
    public void unreferenced() {
        System.out.println("Ya NO existen referencias remotas a esta instancia!");
    }
}
