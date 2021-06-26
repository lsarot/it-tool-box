
package com.lso.test_websocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioCliente {
    
    public static void main(String[] args) {
        
        InicioCliente ref = new InicioCliente();
        
        try {
            Client cl = new Client(new URI("ws://192.168.0.180:5000"));
            //cl.getSocket().setKeepAlive(false);
            //cl.getSocket().setSoTimeout(500); //NULL POINTER EXCEPTION
            cl.setConnectionLostTimeout(500);

            cl.connect();

            Thread.sleep(300);

            //no aparece como open ni close..
            System.out.println(cl.isClosed());
            System.out.println(cl.isClosing());
            System.out.println(cl.isConnecting());
            System.out.println(cl.isFlushAndClose());
            System.out.println(cl.isOpen());
            System.out.println(cl.isReuseAddr());
            //pero al menos esto la cierra
            cl.closeConnection(0, "chao");//ESTO LA CIERRA BIEN
            System.out.println(cl.isClosed());
            cl.getSocket().close();//ESTO LO FULMINA

            if(cl.isConnecting()){
                System.out.println("estaba conectando");
                cl.close();
            }
            
            ref.finalize();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(InicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(InicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(InicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(InicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
