
package main.alternativa;

/**
 *
 * @author Leo
 */
public class Server {
    
    public static void main(String[] args) {
        Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
        discoveryThread.start();
    }
    
}
