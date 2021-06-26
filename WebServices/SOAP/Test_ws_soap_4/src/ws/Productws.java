package ws;

import java.util.List;

import javax.jws.*;

import model.Product;

@WebService
public interface Productws {

	@WebMethod
	public Product getProduct();

	@WebMethod
	public List<Product> getAll();
}
