package dao;

import java.util.ArrayList;
import java.util.List;

import model.Product;

public class ProductDao {

	public Product getProduct() {
		
		return new model.Product("producto_1", 100.10);
	}
	
	public List<Product> getProducts() {
		List<Product> list = new ArrayList<Product>();
		list.add(new model.Product("producto_1", 100.10));
		list.add(new model.Product("producto_2", 120.00));
		list.add(new model.Product("producto_3", 80.05));
		return list;
	}
}
