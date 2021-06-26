package com.example.codigosbasicos.sockets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {
    
    private final String HOST = "localhost";
    private final int PUERTO = 5500;
    private Socket skt;
    
    private OutputStream out0;
    private ObjectOutputStream out1;
    private DataOutputStream out2;
    private PrintWriter out3;
    
    private InputStream in0;
    private ObjectInputStream in1;
    private DataInputStream in2;
    private BufferedReader in3;
    
    public ClientSocketHandler() {}
    
    @Override
    public void run() {
        
        try {
            // conectamos con servidor
            skt = new Socket();
            skt.connect(new InetSocketAddress(HOST, PUERTO), 3000);
            
            //**********
            //*** PARA QUE FUNCIONE, POR ALGUNA RAZÓN, SE DEBE PRIMERO LLAMAR AL OUT Y LUEGO AL IN (PROBADO CON ENVÍO DE OBJETOS) ***
            //**********
            
            // a partir del socket creado podemos usar su inputstream-outputstream para leer-escribir respectivamente
            //out0 = skt.getOutputStream();
            out1 = new ObjectOutputStream(skt.getOutputStream());
            //out2 = new DataOutputStream(skt.getOutputStream());
            //out3 = new PrintWriter(skt.getOutputStream(), true);
            
            //in0 = skt.getInputStream();
            in1 = new ObjectInputStream(skt.getInputStream());
            //in2 = new DataInputStream(skt.getInputStream());
            //in3 = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            
            // fijamos un tiempo máximo de espera sobre la operación read del inputstream asociado a este socket
            skt.setSoTimeout(3000);
            
            //out0.write(byte[] b);
            out1.writeObject(new ObjetoSerializable(10, "texto", (byte)5, new ObjetoSerializable()));   out1.flush();
            //out2 y out3 tienen varios métodos para escribir... out2.writeUTF("&"+"texto"); out3.write("texto");out3.print("texto");  
            
            Object objeto = in1.readObject();
            System.out.println("Objeto regresado a cliente: " + objeto);
            
            in0.close(); in1.close(); in2.close(); in3.close();
            out0.close(); out1.close(); out2.close(); out3.close();   
            skt.close();
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }  
    }
    
}

/* EJEMPLO DE JAVA DE CLIENT SIDE, USANDO TRY-WITH-RESOURCES PARA CERRAR AUTOMATICAMENTE LOS RECURSOS EN CUALQUIER CONDICIÓN QUE SE CULMINE EL BLOQUE TRY

try (
    Socket sock = new Socket(hostName, portNumber);
    PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
) {
    String userInput;
    while ((userInput = stdIn.readLine()) != null) {
        out.println(userInput);
        System.out.println("echo: " + in.readLine());
    }
} catch (UnknownHostException e) { System.err.println("Don't know about host " + hostName);
} catch (IOException e) { System.err.println("Couldn't get I/O for the connection to " + hostName); } 

*/