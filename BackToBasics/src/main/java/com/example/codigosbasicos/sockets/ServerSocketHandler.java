package com.example.codigosbasicos.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler extends Thread {
    
    private final int portNumber = 5500;
    
    @Override
    public void run() {
        try {
            ServerSocket svrSkt = new ServerSocket(portNumber);
            Socket skt = svrSkt.accept();
                
            PrintWriter out0 = new PrintWriter(skt.getOutputStream(), true);
            ObjectOutputStream out1 = new ObjectOutputStream(skt.getOutputStream());
            
            BufferedReader in0 = new BufferedReader(new InputStreamReader(skt.getInputStream()));               
            ObjectInputStream in1 = new ObjectInputStream(skt.getInputStream());
            
            Object objeto = in1.readObject();
            System.out.println("Objeto recibido en servidor: " + objeto);
            out1.writeObject(objeto);
            
            /*String inputLine;
            while((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }*/
            
            in0.close(); in1.close();
            out0.close(); out1.close();
            skt.close(); svrSkt.close();
        } catch (IOException e) { 
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage()); 
        } catch (ClassNotFoundException e) {
            System.out.println("Clase no encontrada");
        }
    }
}
