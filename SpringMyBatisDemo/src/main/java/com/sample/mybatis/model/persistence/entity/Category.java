package com.sample.mybatis.model.persistence.entity;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

    private String category_name;
    private String description;
    private String image_url;

    private List<Product> products;

    public Category() {}

    public Category(String category_name, String description, String image_url) {
        this.category_name = category_name;
        this.description = description;
        this.image_url = image_url;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + category_name + '\'' +
                ", description='" + description + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
