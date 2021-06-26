package ejempbasico;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI {
    
    public static void main(String args[]) {
        
        try {
            //OTRA ALTERNATIVA PARA ENLAZAR CON EL REGISTRY
                //Naming.rebind("serverRMI", new RemoteObject());
            
            Registry reg = LocateRegistry.createRegistry(1099);// 1099 es el puerto del registro por defecto, pero puedo usar cualquier otro.
            reg.rebind("serverRMI", new RemoteObject());
            System.out.println("Server started!");
            
        } catch(RemoteException re) { System.err.println(re); 
        } //catch(MalformedURLException me) { System.err.println(me); }
        
    }
}

/* BREVE EXPLICACIÓN DE LA ESCENCIA DE RMI:
*   UNA INTERFACE QUE EXTIENDE DE Remote Y TIENE LA FIRMA DE LOS MÉTODOS
*   UNA CLASE QUE IMPLEMENTA ESTA INTERFACE Y PUDIERA EXTENDER (SEGÚN EL MÉTODO POSTERIOR DE REGISTRO) DE UnicastRemoteObject
*   SE REGISTRA EN EL RMIREGISTRY UNA INSTANCIA DE LA CLASE IMPLEMENTACIÓN (HAY VARIOS MÉTODOS DE REGISTRO)
*   
*   EL CLIENTE OBTIENE UNA REF DE LA INTERFAZ REMOTA (REALMENTE ES UN STUB/PROXY)
*   LLAMA A LOS MÉTODOS DE DICHA INTERFAZ
*       EL PASE DE PARÁMETROS Y RETORNO ES: COPIA SI ES PRIMITIVO O SERIALIZABLE, REF SI ES OBJ REMOTO

*  SOBRE DESCARGA DINÁMICA DE CLASES:
*   ANTES QUE NADA, SI SERVIDOR O CLIENTE RECIBE UNA INTERFAZ REMOTA (LA ESCENCIA DE RMI), AL LLAMARSE UNO DE SUS MÉTODOS, SE EJECUTARÁ EN EL ORÍGEN, DE DONDE PROVINO LA INTERFAZ.
*   SI SERVIDOR O CLIENTE RECIBEN UNA INTERFAZ NO REMOTA (COMO PARÁMETRO O RETORNO), AL EJECUTARSE UNO DE SUS MÉTODOS:
*       SI LA IMPLEMENTACIÓN ESTÁ GUARDADA LOCALMENTE, SE USA DICHA CLASE.
*       SI NO ESTÁ LOCALMENTE, SE DESCARGARÁ DE CLIENTE O SERVIDOR BIEN SEA EL CASO.
*           ESTO ÚLTIMO NO LO LOGRÉ HACER PQ HAY QUE CONFIGURAR UN SEC MANAGER Y COLOCAR LA CLASE EN UN DIR COMPARTIDO O ACCESIBLE COMO UN WEB SERVER!
*   CLARO ESTÁ QUE CUALQUIER INTERFAZ, REMOTA O NO, DEBE ESTAR PRESENTE EN CLIENTE O SERVIDOR SI ESTOS LA USARÁN, SINO DARÁ ERROR DE COMPILACIÓN, LA DESCARGA ES SÓLO DE LA CLASE QUE IMPLEMENTA LA INTERFACE.

*
*   En proy maquinaremota (ejecuta código en servidor) se pasa interfaz no remota del cliente   (por copia y, el servidor buscará la impl en su propio classpath o descarga dinámica si no la encuentra)
*   En proy messagingapp (mensajería) se pasa interfaz remota de cliente para hacer broadcast de los mensajes    (por ref, se ejecutan métodos remotamente en ambos sentidos)
*   En proy retornoremref (banco) se pasa objeto no remoto, se recibe ref remota a banco y a cuenta y lista de ref de cuentas   (banco y cuentas son remotos, todo por referencia)
*   ¡LOS NOMBRES DE LOS PACKAGE DIFIEREN DE LOS DE RMIinterfaces PQ HABÍA PROBLEMAS AL PONERLOS IGUAL! 
*
*/
