
package websocket_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

public class Websocket_server {

    //INTENTO DE SERVIDOR,
    //no evalúa ping y pong (opcode 9 y a respectivamente)
    //no evalúa close (debo inventar un mensaje que indique que quiere cerrar conexión como dice la especificación)
    //FUNCIONA BIEN CON TEXTO MENOR A 125, MAYOR A 125 Y MENOR A 255+255=510, PERO NO MAYOR DE ESO
    //SI VUELVO A ENVIAR Y EL SERVIDOR ESTÁ FUNCIONANDO SE ENVÍA UNA TRAMA QUE NO ENTENDÍ
    //NO LOGRÉ QUE SIRVA CON BINARY, AL LEER LOS bytes A VECES PRODUCE CON 32bit, A VECES CON 1, OTROS CON 6, NO CONOZCO LA RAZÓN
    //NO SE ENVÍAN DATOS AL CLIENTE, SÓLO SE RECIBEN
    
    private static InputStream in;
    private static OutputStream out;
    private static List<Byte> DECODEDTOTAL = new ArrayList<>();
    private static String tipoTrama = "";
    private static int payloadL = 0;
    private static int inicioPayload = 0;
    private static byte[] mask = new byte[4];
    private static byte[] b = new byte[100000];
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        int port = 5000;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server has started on 127.0.0.1:"+port+".\r\nWaiting for a connection...");
        
        Socket client = server.accept();
        System.out.println("A client connected.");

        in = client.getInputStream();
        out = client.getOutputStream();
        
        //leemos la solicitud y hacemos el handshake respondiendo con los headers necesarios
        //translate bytes of request to string
        String data = new Scanner(in,"UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);
        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + DatatypeConverter.printBase64Binary(
                            MessageDigest.getInstance("SHA-1").digest( (match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8") ) 
                    ) + "\r\n\r\n"
                    ).getBytes("UTF-8");

            out.write(response, 0, response.length);
        }
        
        leerDatos();
        
    }

    private static void leerDatos() throws IOException {
        
        while(true){
            System.out.println("LEYENDO...");     
            int cant = in.read(b);      System.out.println(cant+" bytes totales en trama.");
            
            String byte1 = Integer.toUnsignedString(b[0], 2).substring(24);     System.out.println("byte1: "+byte1);
            int FIN = Integer.parseInt(byte1.substring(0, 1));    System.out.println("FIN: "+FIN);
            
            String opcode = Integer.toHexString(Integer.parseInt(byte1.substring(4), 2));     System.out.println("opcode: "+opcode);
            if(!opcode.equals("0")) { tipoTrama = opcode; } //si no es continuación es que es una nueva (texto:1; binary:2; cont:0)

            setearInicioYlenghtDePayload();//desde dónde y cuánto debo leer
            
            mask[0] = (byte)b[inicioPayload-4]; mask[1] = (byte)b[inicioPayload-3]; mask[2] = (byte)b[inicioPayload-2]; mask[3] = (byte)b[inicioPayload-1]; //seteamos máscara

            decodificarYguardar();//decodificar trama como dice la especificación
            
            //si termina la secuencia de tramas se procesan los datos
            if(FIN == 1){ procesarDatos(); }
        }
        
    }
    
    private static void setearInicioYlenghtDePayload() {
        String _byte2 = Integer.toUnsignedString(b[1], 2).substring(24);     System.out.println("byte2: "+_byte2);
        int masked = Integer.parseInt(_byte2.substring(0,1));     System.out.println("isMasked: "+masked);
        int byte2 = Integer.parseInt(_byte2.substring(1), 2);     System.out.println("payloadLenght: "+byte2);
        //if(masked == 0) ...NO CONECTAR! return 403 forbidden
        
        if(byte2 <= 125) {
            payloadL = byte2;
            inicioPayload = 6;          
        } else if(byte2 == 126) {
            for(int i=0;i<2;i++){
                if( Integer.toUnsignedString(b[2+i], 2).equals("1") )
                    payloadL += 255;
                else if ( !Integer.toUnsignedString(b[2+i], 2).equals("0") )                      
                    payloadL += Integer.parseInt(Integer.toUnsignedString(b[2+i], 2).substring(24), 2);
            }
            inicioPayload = 8;
        } else if (byte2 == 127) {
            for(int i=0;i<8;i++){
                if( Integer.toUnsignedString(b[2+i], 2).equals("1") )
                    payloadL += 255;
                else if ( !Integer.toUnsignedString(b[2+i], 2).equals("0") )
                    payloadL += Integer.parseInt(Integer.toUnsignedString(b[2+i], 2).substring(24), 2);
            }
            inicioPayload = 14;
        }
    }
    
    private static void decodificarYguardar() {
        byte[] DECODED = new byte[ payloadL ];
        for (int z=0;z<payloadL;z++){//decodifico bytes con la máscara
            DECODED[z] = (byte) ( ((byte)b[inicioPayload+z]) ^ mask[z & 0x3] ); //cada byte decoded es: el byte encoded XOR byte_de_mask[z & 0x3]
        }
        for(int z=0;z<payloadL;z++){ //agrego a lista de bytes con todo el contenido
            DECODEDTOTAL.add(DECODED[z]);
        }
    }
    
    private static void procesarDatos() throws FileNotFoundException, IOException {
        //colocamos de la lista a un vector simple de bytes
        byte[] byteY = new byte[DECODEDTOTAL.size()];
        for(int i=0;i<DECODEDTOTAL.size();i++) {
            byteY[i] = DECODEDTOTAL.get(i);                         
        }

        //procesar datos para texto
        if(tipoTrama.equals("1")){//si el contenido era texto        
            String str = new String(byteY, StandardCharsets.UTF_8);
            System.out.println("Contenido: "+ str);
        }

        //ahora si es binario
        if(tipoTrama.equals("2")){ //intento guardar una imágen recibida
            System.out.println("Trama grande!: "+byteY.length);
            FileOutputStream fos = new FileOutputStream( System.getProperty("user.home")+"/Desktop/"+"imagen.jpg");
            fos.write(byteY,0,byteY.length);                         
            fos.close();
        }

        DECODEDTOTAL.clear();//limpiamos la lista si es la trama final
    }

}

/*
    Un servidor WebSocket es explicado a un muy bajo nivel aquí. Los servidores WebSocket usualmente estan separados y especializados (por una cuestión de balance de cargas y otra razones prácticas), 
        por lo tanto deberías usar un Reverse Proxy (semejante a un servidor HTTP común) casi siempre para detectar los Handshakes de WebSocket, preprocesarlos, y reenviar los datos de los clientes al servidor WebSocket real.

    Advertencia: El servidor puede escuchar cualquier puerto que elijas, pero si elijes un puerto diferente al 80 o 443 podría haber problemas con los firewalls y proxies. Suele suceder con el puerto 443 tambien pero para eso se necesita un conexión segura (TLS/SSL). También se debe aclarar que la mayoría de los navegadores (como Firefox 8 o superiores) no permiten conexiones a servidores WebSocket sin seguridad que se realicen desde páginas web con seguridad (HTTPS). 

    Si alguna cabecera no se entiende o posee un valor incorrecto, el servidor debe responder "400 Bad Request" e inmediatamente cerrar la conexión. Normalmente, también puede dar la razón porque falló el handshake en el cuerpo de la respuesta HTTP.
    Todos los navegadores deben enviar una cabecera Origin. Tu puedes usar esta cabecera por seguridad (revisando por el mismo origen, listas blancas/ listas negras, etc.) y enviar un 403 Forbidden si no te gusta lo que ves. Sin embargo, se advierte que los agentes no navegadores pueden enviar un falso Origin.  La mayoría de las aplicaciones rechazaran las solicitudes sin esta cabecera.
    Tu servidor debe llevar el registro de los sockets de los clientes, de manera de no realizar handshakes constantemente con los clientes que ya han completado este proceso. La misma dirección IP cliente puede intentar conectarse múltiples veces (pero el servidor puede denegar la conexión si se intentan demasiadas conexiones con el objetivo de evitar ataques DoS).

    Todos los frames siguen este formato, pero del cliente al servidor siempre deben usar enmascaramiento

    0               1               2               3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-------+-+-------------+-------------------------------+
   |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
   |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
   |N|V|V|V|       |S|             |   (if payload len==126/127)   |
   | |1|2|3|       |K|             |                               |
   +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
   |     Extended payload length continued, if payload len == 127  |
   + - - - - - - - - - - - - - - - +-------------------------------+
   |                               |Masking-key, if MASK set to 1  |
   +-------------------------------+-------------------------------+
   | Masking-key (continued)       |          Payload Data         |
   +-------------------------------- - - - - - - - - - - - - - - - +
   :                     Payload Data continued ...                :
   + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
   |                     Payload Data continued ...                |
   +---------------------------------------------------------------+

    Los RSV1-3 se pueden ignorar, son para las extensiones.

    El bit MASK simplemente indica si el mensaje está codificado. Los mensajes del cliente deben estar enmascarados, por lo que tu servidor debe esperar que valga 1. Cuando se envía una trama al cliente, no lo ocultes y no pongas el bit de la máscara. Nota: Tienes que enmascarar los mensajes incluso cuando uses un socket seguro (de cliente a servidor).
    El campo opcode define cómo interpretar los datos de la carga útil:
        0x0 para continuar, 
        0x1 para texto (que siempre se codifica con UTF-8), 
        0x2 para datos binarios
        0x9 PING
        0xA PONG   When you get a ping, send back a pong with the exact same Payload Data as the ping (for pings and pongs, the max payload length is 125). You might also get a pong without ever sending a ping; ignore this if it happens. Send just 1 pong for n pings you receive.

    Payload lenght:     
        1.Read bits 9-15 (inclusive) and interpret that as an unsigned integer. If it's 125 or less, then that's the length; you're done. If it's 126, go to step 2. If it's 127, go to step 3.
        2.Read the next 16 bits and interpret those as an unsigned integer. You're done.
        3.Read the next 64 bits and interpret those as an unsigned integer. You're done.


    EJEMPLO DE CONVERSACIÓN CLIENTE-SERVIDOR
    Client: FIN=1, opcode=0x1, msg="hello"
    Server: (process complete message immediately) Hi.
    Client: FIN=0, opcode=0x1, msg="and a"
    Server: (listening, new message containing text started)
    Client: FIN=0, opcode=0x0, msg="happy new"
    Server: (listening, payload concatenated to previous message)
    Client: FIN=1, opcode=0x0, msg="year!"
    Server: (process complete message) Happy new year to you too!

    
    To close a connection either the client or server can send a control frame with data containing a specified control sequence to begin the closing handshake (detailed in Section 5.5.1). Upon receiving such a frame, the other peer sends a Close frame in response. The first peer then closes the connection. Any further data received after closing of connection is then discarded. 

    Extensions control the WebSocket frame and modify the payload, 
        while subprotocols structure the WebSocket payload and never modify anything. 
    Extensions are optional and generalized (like compression); subprotocols are mandatory and localized (like ones for chat and for MMORPG games).

*/
