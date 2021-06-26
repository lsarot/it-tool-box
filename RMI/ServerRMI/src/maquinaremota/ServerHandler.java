
package maquinaremota;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerHandler {
    //IMPORTANTE SOBRE CONFIGURACIÓN DE SERVIDOR Y CLIENTE SI USAS DESCARGA DINÁMICA DE CLASES
    // https://docs.oracle.com/javase/tutorial/rmi/running.html
    
    //NO ME ESTÁ FUNCIONANDO EL CLIENTE POR ALGUNA RAZÓN CON EL SEC MANAGER, PERO EL CÓDIGO ESTÁ BIEN 
    
    public static void main(String[] args) {
        //---------------------------------- SETEANDO SEC MANAGER
        //PARA QUE EL CÓDIGO (CLASES) DESCARGADO DINAMICAMENTE PUEDA REALIZAR OPERACIONES DE DETERMINADA ÍNDOLE EN LA MÁQUINA DESTINO (ÉSTA)
        
        //SUPONIENDO QUE ESTÁN GUARDADOS EN EL DIRECTORIO DEL SERVIDOR MAMP
        
        
        System.setProperty("java.rmi.server.hostname", "http://localhost");

        //System.setProperty("java.rmi.server.codebase", "http://localhost:80/Z_servidorRMI/computeengine/");
        //System.setProperty("java.rmi.server.codebase", "file:/c:/home/jones/public_html/classes/");
        //System.setProperty("java.rmi.server.codebase", "file:/c:/home/ann/public_html/classes/compute.jar");
        //System.setProperty("java.rmi.server.codebase", "file://Applications/MAMP/htdocs/ClientRMI/dist/ClientRMI.jar");
        //System.setProperty("java.rmi.server.codebase", "file://Applications/MAMP/htdocs/ClientRMI/src/maquinaremota/"); 
        //java.rmi.server.codebase="http://webfront/myStuff.jar http://webwave/myOtherStuff.jar http://webvector/export/"
        System.setProperty("java.rmi.server.codebase", "http://localhost/ServerRMI/build/classes/maquinaremota/");
        
        //System.setProperty("java.security.policy", "server.policy");
        //System.setProperty("java.security.policy", "http://localhost/ServerRMI/build/classes/maquinaremota/server.policy");
        //System.setProperty("java.security.policy", "file:///Applications/MAMP/htdocs/ServerRMI/build/classes/maquinaremota/server.policy");
        System.setProperty("java.security.policy", "file:./build/classes/maquinaremota/server.policy");
        
        
        
        
        if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }
        
        //---------------------------------- REGISTRANDO REM OBJ EN REGISTRY
        //RECORDAR QUE REGISTRY ES UN REMOTE OBJECT, SE LEVANTA UN WEB SERVER LOCAL PONIENDO A DISPOSICIÓN EL REGISTRY QUE PROVEE LOS NOMBRES DE LOS OBJETOS REMOTOS DE ESTA MÁQUINA
        //EL REGISTRY SÓLO PUEDE SER MODIFICADO DESDE LA MÁQUINA LOCAL, NUNCA DESDE UNA REMOTA (POR SEGURIDAD)
        try {
            
            //OTRA ALTERNATIVA PARA ENLAZAR CON EL REGISTRY
                //Naming.rebind("ComputeEngine", new ComputeEngine());
            
            Registry reg = LocateRegistry.createRegistry(1099);//puerto por defecto, si usamos getRegistry también sirve!         
            
            //COMO ComputeEngine (LA IMPL DE LA INTERFAZ REMOTA) EXTIENDE DE UnicastRemoteObject, PODEMOS ENLAZARLA CON UNA INSTANCIA DE ESTA DIRECTAMENTE
                reg.rebind("ComputeEngine", new ComputeEngine());
            
                //SI NO EXTIENDE DE UnicastRemoteObject, DEBEMOS ENLAZARLA CON EL OBJETO DEVUELTO POR ESTE MÉTODO  (SE USA SI QUEREMOS EXTENDER DE OTRA CLASE)
                    //Compute stub = (Compute) UnicastRemoteObject.exportObject(new ComputeEngine(),1099);
                    //reg.rebind("ComputeEngine", stub);
            
            System.out.println("ComputeEngine bound!");
            
            String[] list = reg.list(); System.out.println("Registros:"); for(String s:list){ System.out.println(s); }
            
        } catch (Exception e) { System.err.println("ComputeEngine exception:"); System.err.println(e); }
        //----------------------------------
    }
    
}

/*

- En este ejemplo se utiliza la descarga dinámica de clases:
  se descargará MiTarea (impl de interface Task) del cliente hacia el servidor, por ello requiere el uso de un securitymanager, el cual implica el uso de un archivo
  con políticas de seguridad establecidas, en este caso no se ha terminado de crear, puesto que hace falta la ruta completa.
    Lo mismo aplica para el codebase, ruta desde la cual se descargarán las clases, si alguien solicita una clase a esta jvm, pero esta ruta debe ser accesible
    desde internet, utilizando un web server o una ruta compartida; en el primer caso se usa una url tipo http:, en el segundo caso una url tipo file:, 
    pero aún no se colocan correctamente. 
//LAS INTERFACES COMPUTE Y TASK DEBEN ESTAR PRESENTES EN SERVIDOR Y CLIENTE, LA DESCARGA DINÁMICA ES DE MiTarea, DESDE CLIENTE HACIA EL SERVIDOR!

** Passing Objects in RMI

    Arguments to or return values from remote methods can be of almost any type, including local objects, remote objects, and primitive data types. More precisely, 
    any entity of any type can be passed to or from a remote method as long as the entity is an instance of a type that is a primitive data type, a remote object, 
    or a serializable object, which means that it implements the interface java.io.Serializable.

    Some object types do not meet any of these criteria and thus cannot be passed to or returned from a remote method. Most of these objects, such as threads or file
    descriptors, encapsulate information that makes sense only within a single address space. Many of the core classes, including the classes in the packages java.lang 
    and java.util, implement the Serializable interface.

    The rules governing how arguments and return values are passed are as follows:
        Remote objects are essentially passed by reference. A remote object reference is a stub, which is a client-side proxy that implements the complete set of remote 
            interfaces that the remote object implements.
        Local objects are passed by copy, using object serialization. By default, all fields are copied except fields that are marked static or transient. Default 
            serialization behavior can be overridden on a class-by-class basis.

    Passing a remote object by reference means that any changes made to the state of the object by remote method invocations are reflected in the original remote object.
    When a remote object is passed, only those interfaces that are remote interfaces are available to the receiver. Any methods defined in the implementation class or defined
    in non-remote interfaces implemented by the class are not available to that receiver.

    For example, if you were to pass a reference to an instance of the ComputeEngine class, the receiver would have access only to the compute engine's executeTask method.
    That receiver would not see the ComputeEngine constructor, its main method, or its implementation of any methods of java.lang.Object.

    In the parameters and return values of remote method invocations, objects that are not remote objects are passed by value. Thus, a copy of the object is created in the
    receiving Java virtual machine. Any changes to the object's state by the receiver are reflected only in the receiver's copy, not in the sender's original instance. Any
    changes to the object's state by the sender are reflected only in the sender's original instance, not in the receiver's copy.

** Implementing the Server's main Method

    The most complex method of the ComputeEngine implementation is the main method. The main method is used to start the ComputeEngine and therefore needs to do the necessary 
    initialization and housekeeping to prepare the server to accept calls from clients. This method is not a remote method, which means that it cannot be invoked from a different
    Java virtual machine. Because the main method is declared static, the method is not associated with an object at all but rather with the class ComputeEngine.

** Creating and Installing a Security Manager

    The main method's first task is to create and install a security manager, which protects access to system resources from untrusted downloaded code running within the
    Java virtual machine. A security manager determines whether downloaded code has access to the local file system or can perform any other privileged operations.

    If an RMI program does not install a security manager, RMI will not download classes (other than from the local class path) for objects received as arguments or return
    values of remote method invocations. This restriction ensures that the operations performed by downloaded code are subject to a security policy.

    For both example policy files, all permissions are granted to the classes in the program's local class path, because the local application code is trusted, 
    but no permissions are granted to code downloaded from other locations. SEE server.policy y client.policy
*/