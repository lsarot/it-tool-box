
package remoterefreturn;

import java.io.Serializable;

/* Esta clase representa un objeto que se recibe como parámetro y se retorna en un método remoto del servidor
 * Al no ser un objeto remoto, sino un objeto que se envía a través de RMI como copia, debe ser Serializable
 */
public class TitularCuenta implements Serializable {
    
    private String nombre;
    private String iD;
    
    public TitularCuenta(String n, String i) {
        nombre = n;
        iD = i;
    }
    
    public String obtenerNombre() {
        return nombre;
    }
    
    public String obtenerID() {
        return iD;
    }
    
    @Override
    public String toString() {
        return nombre + " | " + iD;
    }
}
