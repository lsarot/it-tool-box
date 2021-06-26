
package ejempbasico;

import ejemplobasico.RMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientHandler {
    
    public static void main(String[] args) {
    
        try {
            
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);//el registry se puede obtener de una dirección remota (externa)
            RMI svrremref = (RMI) registry.lookup("serverRMI"); 
                
            String resultado = svrremref.metodo("TEXTO ENVIADO AL SERVIDOR!");
            System.out.println("Resultado: "+resultado);
            
        } catch (Exception e) { System.err.println("Client exception: "); e.printStackTrace(); }
    
    }
    
}
