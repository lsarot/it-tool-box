package ws;

import java.util.List;

import javax.jws.WebService;

import dao.ProductDao;
import model.Product;

@WebService(endpointInterface="ws.Productws")
public class ProductwsImpl implements Productws {

	private ProductDao productDao = new ProductDao();
	
	@Override
	public Product getProduct() {
		return this.productDao.getProduct();
	}

	@Override
	public List<Product> getAll() {
		return this.productDao.getProducts();
	}

}
