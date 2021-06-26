package ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Demo {

	@WebMethod
	public String hola();
	
	@WebMethod
	public String echo(String s);
	
}
