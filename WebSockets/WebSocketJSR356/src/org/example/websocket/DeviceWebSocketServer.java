
package org.example.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.example.model.Device;
    
@ApplicationScoped
@ServerEndpoint("/actions") //ESTA ES LA ESCENCIA DEL SERVIDOR WEBSOCKET JEE ( se accede desde http://host/WebsocketHome/actions )
public class DeviceWebSocketServer {

    @Inject
    private DeviceSessionHandler sessionHandler; //manejador de conexiones(Sessions) y de objetos Device(bean a enviar para comunicarse)
    
    //CADA UNO DE LOS MÉTODOS CALLBACK DE WEBSOCKET (mapeados con annotations)
    
    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session); //añadimos a una lista
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session); //quitamos de la lista
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) { //message viene en formato JSON stringified, por eso es texto. Websockets permite binarios también.
        sessionHandler.handleMessage(message, session);
    }
}    