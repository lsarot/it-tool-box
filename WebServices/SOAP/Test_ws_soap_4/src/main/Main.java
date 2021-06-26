package main;

import javax.xml.ws.Endpoint;

public class Main {

	public static void main(String[] args) {
		Endpoint.publish("http://localhost:8175/ws/demows", 
				new ws.DemoImpl());
		Endpoint.publish("http://localhost:8175/ws/product", 
				new ws.ProductwsImpl());
		System.out.println("Done!");
	}

}
