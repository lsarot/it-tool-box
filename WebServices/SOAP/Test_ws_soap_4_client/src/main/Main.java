package main;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import ws.Product;
import ws.Productws;
import ws.ProductwsImplService;
import ws.ProductwsImplServiceLocator;

public class Main {

	public static void main(String[] args) throws RemoteException, ServiceException {
		ProductwsImplService pwsi = new ProductwsImplServiceLocator();
		Productws port = pwsi.getProductwsImplPort();
		Product product = port.getProduct();
		System.out.println("Nombre: " + product.getNombre());
		System.out.println("Precio: " + product.getPrecio());
		System.out.println("==============================");
		Product[] lista = port.getAll();
		for(Product p : lista) {
			System.out.println("Nombre: " + p.getNombre());
			System.out.println("Precio: " + p.getPrecio());
			System.out.println("==============================");
		}
	}

}
