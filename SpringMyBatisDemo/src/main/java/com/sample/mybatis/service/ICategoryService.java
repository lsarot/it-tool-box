package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Category;
import com.sample.mybatis.model.persistence.entity.Contact;

public interface ICategoryService {

    Category getByCategoryId(final String categoryId);
    List<Category> getAllCategories();
    void addCategory(final Category contact);
    void updateCategory(final Category contact);
    void deleteCategory(final String categoryId);

}
