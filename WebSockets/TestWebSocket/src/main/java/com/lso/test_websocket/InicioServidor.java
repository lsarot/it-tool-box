
package com.lso.test_websocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioServidor {
 
    public static void main(String[] args){
        Server s = new com.lso.test_websocket.Server(5000);
        s.start();
        
        try {
            Thread.sleep(10000);
            
            s.stop();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(InicioServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InicioServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}