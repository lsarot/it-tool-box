
package com.lso.test_websocket;

import com.google.gson.Gson;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
        
    }

    @Override
    public void onOpen(ServerHandshake sh) {       
        System.out.println("Conexión iniciada!");
        Entity entity = new com.lso.test_websocket.Entity("Leo", "mi direction", 9434404);
        //no sirve el attachment
        this.getConnection().setAttachment(entity);
        //this.getConnection().send("HOLA desde el cliente!");
        //el objeto Entity de attachment no se recibe en servidor, es null, y eso que se usa Serializable

        //ahora con Json como texto usando Gson library
        Gson gson = new Gson();
	String representacionJSON = gson.toJson(entity);      
        this.getConnection().send(representacionJSON);
    }

    @Override
    public void onMessage(String string) {
        System.out.println("Mensaje recibido: "+string);
    }

    @Override
    public void onClose(int i, String string, boolean bln) {
        System.out.println("Conexión cerrada!");
        try {
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onError(Exception excptn) {
        System.out.println("Error de conexión!");
    }
    
}
