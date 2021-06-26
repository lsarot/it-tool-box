
package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author Leo
 */
public class Server {
    public static void main(String[] args){
        
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            
            byte[] buf = new byte[256];
            MulticastSocket socket = new MulticastSocket(5001);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
            
            while (true) {
                DatagramPacket packet1 = new DatagramPacket(buf, buf.length);
                System.out.println("\nEsperando conexión..");
                socket.receive(packet1);
                System.out.println("Mensaje recibido!");
                String received = new String(packet1.getData(), 0, packet1.getLength());
                
                System.out.println(received);
                System.out.println(packet1.getAddress());
                System.out.println(packet1.getPort());
                System.out.println(packet1.getSocketAddress());
                
                
                //response               
                System.out.println("Enviando respuesta..");              
                InetAddress receiver = packet1.getAddress();//la dirección del cliente
                byte[] buf2 = InetAddress.getLocalHost().getHostAddress().getBytes();//la dirección de este equipo
                DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length, receiver, packet1.getPort());
                socket.send(packet2);
                System.out.println("Respuesta enviada!");
                
                
                if ("end".equals(received)) {
                    break;
                }
            }
            
            socket.leaveGroup(group);
            socket.close();
            
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
