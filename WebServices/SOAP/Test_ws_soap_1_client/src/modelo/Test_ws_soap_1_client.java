package modelo;

import java.rmi.RemoteException;

import com.LSO.examples.ws.soap.controller.LoginImpl;
import com.LSO.examples.ws.soap.controller.LoginImplProxy;
import com.LSO.examples.ws.soap.model.BeanLogin;

public class Test_ws_soap_1_client {

	public static void main(String[] args) {
		LoginImpl iface = new LoginImplProxy("http://localhost:8080/test_ws_soap_1/services/LoginImpl"); //interface = new suimplementación()
        BeanLogin obj = new BeanLogin(); //objeto bean usamos para enviar y recibir luego otro
        obj.setUser("root");
        obj.setPass("0000");
        try {
			obj = iface.validarLogin(obj);//le enviamos el bean y la respuesta la guardamos en el mismo obj por facilidad
		} catch (RemoteException e) {
		} 
        System.out.println(obj.getMensaje());
	}

}
