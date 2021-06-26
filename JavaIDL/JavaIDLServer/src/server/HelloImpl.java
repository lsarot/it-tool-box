package server;

import com.leo.skelstub.HelloApp.*;
import org.omg.CORBA.*;

public class HelloImpl extends HelloPOA { //EN MODELO POA
//public class HelloImpl implements HelloOperations { //EN MODELO TIE
    private ORB orb;

    public void setORB(ORB orb_val) { orb = orb_val; }

    // implement sayHello() method
    @Override
    public String sayHello() {
        return "\nHello desde servidor!!\n";
    }

    // implement shutdown() method
    @Override
    public void shutdown() {
        orb.shutdown(false);
    }
    
}
