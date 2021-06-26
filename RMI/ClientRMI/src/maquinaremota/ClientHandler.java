
package maquinaremota;

import computeengine.Compute;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientHandler {
    
    public static void main(String args[]) {
        //---------------------------------- SETEANDO SEC MANAGER     
        
        //SUPONIENDO QUE ESTÁN GUARDADOS EN EL DIRECTORIO DEL SERVIDOR MAMP
        
        
        System.setProperty("java.rmi.server.hostname", "http://localhost");
        
        //System.setProperty("java.rmi.server.codebase", "http://localhost:80/Z_servidorRMI/computeengine/");
        //System.setProperty("java.rmi.server.codebase", "file:/c:/home/jones/public_html/classes/");
        //System.setProperty("java.rmi.server.codebase", "file:/c:/home/ann/public_html/classes/compute.jar");
        //System.setProperty("java.rmi.server.codebase", "file://Applications/MAMP/htdocs/ClientRMI/dist/ClientRMI.jar");
        //System.setProperty("java.rmi.server.codebase", "file://Applications/MAMP/htdocs/ClientRMI/src/maquinaremota/"); 
        //java.rmi.server.codebase="http://webfront/myStuff.jar http://webwave/myOtherStuff.jar http://webvector/export/"
        System.setProperty("java.rmi.server.codebase", "http://localhost/ClientRMI/build/classes/maquinaremota/");
        
        //System.setProperty("java.security.policy", "server.policy");
        //System.setProperty("java.security.policy", "http://localhost/ServerRMI/build/classes/maquinaremota/server.policy");
        //System.setProperty("java.security.policy", "file:///Applications/MAMP/htdocs/ClientRMI/build/classes/maquinaremota/client.policy");
        System.setProperty("java.security.policy", "file:./build/classes/maquinaremota/client.policy");
        
        //You can also put it in the same folder as your project root), to reduce the URI to
            // file:./<filename>.policy
        //grant codeBase "file:/<path>/bin/-" {
            
            
        
        
        if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }     
        
        //----------------------------------
        try {
            
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);//el registry se puede obtener de una dirección remota (externa)
            Compute svrremref = (Compute) registry.lookup("ComputeEngine"); 
                //ARROJA EXCEPTION, ALGO RELACIONADO AL SEC MANAGER Y SU CONFIG
            
            //----------- LLAMADO REMOTO
            MiTarea task = new MiTarea();
            float result = svrremref.executeTask(task);
            
            System.out.println("Resultado calculado en compute engine (MÁQUINA REMOTA): " + result);
            //-----------
            
        } catch (Exception e) { System.err.println("Client exception: "); e.printStackTrace(); }
        //----------------------------------
    }    
    
}