package ws;

import javax.jws.WebService;

@WebService(endpointInterface="ws.Demo")
public class DemoImpl implements Demo {

	@Override
	public String hola() {
		return "hola";
	}

	@Override
	public String echo(String s) {
		return s;
	}

}
