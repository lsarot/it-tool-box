
package teoria.websocket.jsr356;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.example.websocket.DeviceSessionHandler;
import org.example.websocket.DeviceWebSocketServer;

/** JSR 356, Java API for WebSocket
 * http://www.oracle.com/technetwork/articles/java/jsr356-1937161.html
 *
 * For many Web-based client-server applications, the old HTTP request-response model has its limitations. Information has to be transmitted from the server to the client in between requests, rather than upon request only.
 * A number of "hacks" have been used in the past to circumvent this problem, for example, long polling and Comet. However, the need for a standards-based, bidirectional and full-duplex channel between clients and a server has only increased.
 * Once conn is made, No headers or cookies are required, which considerably lowers the required bandwidth. Typically, WebSockets are used to periodically send small messages (for example, a few bytes). Additional headers would often make the overhead larger than the payload.
 *
 * se puede trabajar con clientes de otro lenguaje, siempre que la implementación ws sea compliant with RFC 6455
 * 
 * Se puede hacer con ANNOTATIONS O INTERFACE
 */

@ServerEndpoint("/server_endpoint/{param-1}")
public class WebSocketServer {
    
    
    //PODEMOS INJECTAR UNA CLASE QUE GUARDE LAS SESSIONS EN UN SET Y SIRVA PARA ENVIAR A TODAS UN MENSAJE POR EJEMPLO
    @Inject
    private DeviceSessionHandler sessionHandler;//REUSAMOS LA CLASE DEL OTRO EJERCICIO, pero tenemos que hacer una adaptada a cada caso
    
    
    
    /* This method can contain a number of parameters:
     * A javax.websocket.Session parameter, specifying the created Session
     * An EndpointConfig instance containing information about the endpoint configuration
     * Zero or more string parameters annotated with @PathParam, referring to path parameters on the endpoint path
    */
    /* The Session class contains a number of interesting methods that allow developers to obtain more information about
       the connection. Also, the Session contains a hook to application-specific data, by means of the getUserProperties() 
       method returning a Map<String, Object>. This allows developers to populate Session instances with session- and application-specific 
       information that should be shared among method invocations.
    */
    @OnOpen
    public void myOnOpen (Session session) {//acepta EndpointConfig y PathParams como dice arriba
       System.out.println("WebSocket opened: "+session.getId());
    }
    
    
    
    /* Puede recibir:
    The javax.websocket.Session parameter.
    Zero or more string parameters annotated with @PathParam, referring to path parameters on the endpoint path.
    * The method parameter may be of type String, any Java primitive type or any boxed version thereof
    The message itself. See below for an overview of possible message types.
    */
    @OnMessage
    public void myOnMessage (Session session, @PathParam("param-1") String param1, String message) throws IOException {//puede retornar el método (no void)
        //pudiera ser @PathParam("param-1") Integer param1
        
        System.out.println("WebSocket received message: "+message);
        
        RemoteEndpoint.Basic endpoint = session.getBasicRemote();
        
        
        //ENVIAMOS BINARIO
        InputStream initialStream = new FileInputStream(new File("/Users/Leo/Desktop/ruta.jpg"));
        byte[] buffer = new byte[initialStream.available()];
        int n = initialStream.read(buffer);
        initialStream.close();
        //enviamos así
        //endpoint.sendBinary(ByteBuffer.wrap(buffer));
        //o así
        //OutputStream outstream = endpoint.getSendStream();
        //outstream.write(buffer);
        //outstream.close();
        
        for(int i=0;i<20;i++) {
            endpoint.sendBinary(ByteBuffer.wrap(buffer));
        }
        
        
        
        //ENVIAMOS TEXTO
        //endpoint.sendText(message.toUpperCase());
        
        
        /* If the return type of the method annotated with @OnMessage is not void,
        the WebSocket implementation will send the return value to the other peer.
        
        return "response";
        */
    }
    
    
    
    /* Puede recibir:    
    The javax.websocket.Session parameter. Note that this parameter cannot be used once the WebSocket is really closed, which happens after the @OnClose annotated method returns.
    A javax.websocket.CloseReason parameter describing the reason for closing the WebSocket, for example, normal closure, protocol error, overloaded service, and so on.
    Zero or more string parameters annotated with @PathParam, referring to path parameters on the endpoint path.
    */
    @OnClose
    public void myOnClose (CloseReason reason) {
       System.out.println("Closing a WebSocket due to "+reason.getReasonPhrase());
    }
    
    
    
    @OnError
    public void myOnError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
    
}

    //Message Types, Encoders, and Decoders
    /*
    Basically, there are three different types of messages:
    - Text-based messages
    - Binary messages
    - Pong messages, which are about the WebSocket connection itself

    YA SEA USANDO ANNOTATIONS O INTERFACE, SE PERMITE MAX 1 MÉTODO POR CADA TIPO (máx 3 entonces)

    
    "if the method is handling text messages:

        .String to receive the whole message
        .Java primitive or class equivalent to receive the whole message converted to that type
        .String and boolean pair to receive the message in parts
        .Reader to receive the whole message as a blocking stream
        .any object parameter for which the endpoint has a text decoder (Decoder.Text or Decoder.TextStream).
    
    if the method is handling binary messages:

        .byte[] or ByteBuffer to receive the whole message
        .byte[] and boolean pair, or ByteBuffer and boolean pair to receive the message in parts
        .InputStream to receive the whole message as a blocking stream
        .any object parameter for which the endpoint has a binary decoder (Decoder.Binary or Decoder.BinaryStream).
    
    if the method is handling pong messages:

        .PongMessage for handling pong messages"


        PARA OBJETOS SE DEBERÍA USAR XML O JSON,
            pero el API permite usar encoder y decoder, lo cual lo hace más laborioso:
            un ejemplo sería:
                    @ServerEndpoint(value="/endpoint", encoders = MessageEncoder.class, decoders= MessageDecoder.class)
                    public class MyEndpoint {}
                
                    class MessageEncoder implements Encoder.Text<MyJavaObject> {
                       @override
                       public String encode(MyJavaObject obj) throws EncodingException {
                          ...
                       }
                    }

                    class MessageDecoder implements Decoder.Text<MyJavaObject> {
                        @override 
                        public MyJavaObject decode (String src) throws DecodeException {
                           ...
                        }

                        @override 
                        public boolean willDecode (String src) {
                           // return true if we want to decode this String into a MyJavaObject instance
                        }
                    }


    */