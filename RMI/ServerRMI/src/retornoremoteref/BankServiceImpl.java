
package retornoremoteref;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import remoterefreturn.BankServiceInterf;
import remoterefreturn.Cuenta;
import remoterefreturn.TitularCuenta;

public class BankServiceImpl extends UnicastRemoteObject implements BankServiceInterf {

    private List<Cuenta> list;
    
    public BankServiceImpl() throws RemoteException {
        super();
        list = new LinkedList<>();
    }

    @Override
    public Cuenta crearCuenta(TitularCuenta t) throws RemoteException {
        System.out.println("Creando cuenta de " + t);
        Cuenta c = new CuentaImpl(t);
        list.add(c);
        return c;
    }

    @Override
    public List<Cuenta> getCuentas() throws RemoteException {
        return list;
    }
    
}
