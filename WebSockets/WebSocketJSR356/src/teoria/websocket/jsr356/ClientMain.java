
package teoria.websocket.jsr356;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.DeploymentException;

public class ClientMain {
    
    public static void main(String[] args) {
       
        try {            
            //UN INCONVENIENTE ES QUE HAY QUE PASAR SIEMPRE EL O LOS PATH PARAMS PQ NO SE PUEDEN PONER EN CADA MÉTODO SINO SOBRE LA CLASE
            //EN WEB SERVICE PUEDO USAR QUERY PARAMS
            //PERO NO ES OBLIGATORIO COMUNICAR POR PARAMS!
            javax.websocket.WebSocketContainer container = javax.websocket.ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8080/Websocket_JSR356/server_endpoint/param1");
            container.connectToServer(WebSocketClient.class, uri);
            
            try {Thread.sleep(3000);} catch(Exception e) {}
            //CUANDO TERMINE LA APP CLIENTE SE CIERRA LA CONEXIÓN!
            //O LA CERRAMOS EN ALGÚN MÉTODO DEL CICLO DE VIDA DEL WEBSOCKET TRÁS ALGÚN MSJE ESPECIAL!
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DeploymentException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
