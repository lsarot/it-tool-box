
package retornoremoteref;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import remoterefreturn.BankServiceInterf;
import remoterefreturn.Cuenta;
import remoterefreturn.TitularCuenta;

public class BankClient {
    
    public static void main(String args[]) throws RemoteException, NotBoundException {
        
        Registry reg = LocateRegistry.getRegistry("localhost", 1099);// "127.0.0.1" "localhost"
        BankServiceInterf srv = (BankServiceInterf) reg.lookup("BankService");
        
        /* Al enviar new TitularCuenta al método remoto crearCuenta, como TitularCuenta no es un objeto remoto, se envía como copia serializada.
        *      Los cambios a esta instancia en el otro lado, no afectarán a este objeto, ya que se envió una copia.
        * Por otro lado se recibe un objeto que sí es remoto (la interfaz Cuenta) (se pasa como referencia y no copia)
        *      Los cambios o llamados a esta ref, tendrán efecto en el otro lado, ya que es una ref remota.
        */
        
        Cuenta cta = srv.crearCuenta(new TitularCuenta("Leonardo","0108 ... 0045"));//Cuenta y Titularcuenta deben estar en este proy pq daría error de compilación
        cta.depositar(5000);
        //ahora creamos otra cuenta
        cta = srv.crearCuenta(new TitularCuenta("Rocky","0105 ... 0453"));
        cta.depositar(10000);
        
            // AQUÍ SE TRABAJA CON EL TEMA DE DESCARGA DINÁMICA DE CLASES
            //cta = srv.crearCuenta(new TitularMenor("Niño","1111 ... 0000","Padre"));
            //cta.depositar(100);
        
        // Igualmente sucede aquí, se recupera una lista de referencias remotas de objetos tipo Cuenta.
        List<Cuenta> lista = srv.getCuentas();
        for(Cuenta c : lista) {
            // A cada ref remota se le solicita un objeto que no es remoto (titular cuenta), este es enviado del otro lado como copia serializada.
            TitularCuenta t = c.getTitular();
            System.out.println("Cuenta " + t + " : " + c.getSaldo() + " $.");
        }
    }
    
}
