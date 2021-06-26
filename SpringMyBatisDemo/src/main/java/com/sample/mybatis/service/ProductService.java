package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Product;
import com.sample.mybatis.model.persistence.mappers_mybatis.ProductMapper;

import java.util.List;

@Service
public class ProductService implements IProductService {


    @Autowired
    private ProductMapper productMapper;


    @Override
    public void addProduct(final Product product) {
        productMapper.insert(product);
    }

    @Override
    public Product getByProductId(int productId) {
        return productMapper.getById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.getAll();
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.update(product);
    }

    @Override
    public void deleteProduct(int productId) {
        productMapper.deleteById(productId);
    }

}
