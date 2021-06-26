
package teoria.websocket.jsr356;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCode;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import org.example.websocket.DeviceWebSocketServer;

@ClientEndpoint
public class WebSocketClient {
    
    @OnOpen
    public void myOnOpen (Session session) throws IOException {//acepta EndpointConfig y PathParams como dice arriba
       System.out.println("WebSocket opened: "+session.getId());
       
       session.getBasicRemote().sendText("mensaje del cliente");
       
    }
 
    
    @OnMessage
    public void myOnMessage (Session session, String txt) throws IOException {//puede retornar el método (no void)
        System.out.println("WebSocket received message: "+txt);

            //session.close();

        //RemoteEndpoint.Basic remep = session.getBasicRemote();
        //remep.sendText("Hello, world");

        /* If the return type of the method annotated with @OnMessage is not void,
         the WebSocket implementation will send the return value to the other peer.

        return "response";
        */
    }
    
    @OnMessage
    public void myOnMessage (Session session, byte[] data) throws FileNotFoundException, IOException {
        System.out.println("mensaje binario: "+ data.length);
        File dir = new File("/Users/Leo/Desktop/prueba");
        int qfiles = dir.list().length;
        
        OutputStream outStream = new FileOutputStream(new File(dir, "/ruta_"+(qfiles+1)+".jpg"));
        outStream.write(data);
        outStream.close();
        
    }
    
    
    @OnClose
    public void myOnClose (CloseReason reason) {
        System.out.println("Closing a WebSocket due to "+reason.getReasonPhrase());
    }
    
    
    @OnError
    public void myOnError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    
}
