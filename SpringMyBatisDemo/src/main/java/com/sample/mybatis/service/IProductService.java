package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Product;

public interface IProductService {

    Product getByProductId(final int productId);
    List<Product> getAllProducts();
    void addProduct(final Product product);
    void updateProduct(final Product product);
    void deleteProduct(final int productId);

}
