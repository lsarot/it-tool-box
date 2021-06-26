
package com.lso.test_websocket;

import com.google.gson.Gson;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

//https://github.com/TooTallNate/Java-WebSocket

public class Server extends WebSocketServer {
    
    public Server(int port) {
        super(new InetSocketAddress(port));
        System.out.println("Recibiendo conexiones en el puerto "+port);
    }
    
    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        ws.send("HOLA desde el servidor!");
    }

    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln) {
        System.out.println("Se ha cerrado la conexión!");
    }

    @Override
    public void onMessage(WebSocket ws, String string) {
        ws.send("Gracias por el mensaje!");
        System.out.println("Mensaje recibido: "+string);
        com.lso.test_websocket.Entity entity = ws.getAttachment();
        System.out.println(entity);
        
        Gson gson = new Gson();
        Entity entity2 = gson.fromJson(string, Entity.class);
        System.out.println(entity2.toString());
        
        ws.send("**MENSAJE DEL SERVIDOR**");
    }

    @Override
    public void onError(WebSocket ws, Exception excptn) {
        System.out.println("Error de conexión!");
    }

    @Override
    public void onStart() {
        
    }
    
}
