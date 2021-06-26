
package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */

//BROADCAST Y MULTICAST
public class Client {
    public static void main(String[] args) {
        
        //http://www.baeldung.com/java-broadcast-multicast
        
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress group = InetAddress.getByName("230.0.0.0");
            byte[] buf = "GIVE_ME_YOUR_IP_PLEASE".getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 5001);
            socket.send(packet);
            System.out.println("Mensaje enviado!");
            
            System.out.println("Esperando respuesta..");
            byte[] buf2 = new byte[256];
            DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length);
            socket.setSoTimeout(4000);
            socket.receive(packet2);
            String received = new String(packet2.getData(), 0, packet2.getLength());
            System.out.println(received);
            
            
            socket.close();
            
        } catch (SocketException ex) {
            System.out.println("se"+ex);
        } catch (UnknownHostException ex) {
            System.out.println("uhe"+ex);
        } catch (IOException ex) {
            System.out.println("ioe"+ex);
        }
        
        
        //----------------- BROADCAST.. no recomendado pq envía a todos, multicast en cambio permite un grupo y es más eficiente.. de hecho IPV6 no usa broadcast parece ser
        
        //EXACTAMENTE IGUAL QUE EL ANTERIOR PERO USAMOS 255.255.255.255
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            InetAddress address = InetAddress.getByName("255.255.255.255");
            byte[] buffer = "HOLA".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 5000);
            socket.send(packet);
            socket.close();
        
        } catch (SocketException ex) {           
        } catch (UnknownHostException ex) {
        } catch (IOException ex) {          
        }
        
        try {
            List<InetAddress> broadcastList = listAllBroadcastAddresses();
            for(InetAddress ad : broadcastList) {
                System.out.println("BroadcastAddress: "+ad);
            }
            
        } catch (SocketException ex) {
        }
        
    }    
    
    //BROADCAST SE HACE A LA SUBRED DEL ROUTER AL QUE ESTÉ CONECTADO, SI QUEREMOS QUE LO HAGA A TODA LA RED DEBEMOS ITERAR SOBRE N DIRECCIONES DE RED, ESTE MÉTODO LAS DEVUELVE, LUEGO LLAMAMOS CADA VEZ AL MÉTODO BROADCAST
    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
            networkInterface.getInterfaceAddresses().stream() 
              .map(a -> a.getBroadcast())
              .filter(Objects::nonNull)
              .forEach(broadcastList::add);
        }
        return broadcastList;
    }
    
}
