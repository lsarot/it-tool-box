package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Category;
import com.sample.mybatis.model.persistence.mappers_mybatis.CategoryMapper;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {


    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public void addCategory(final Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public Category getByCategoryId(String categoryId) {
        return categoryMapper.getById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.getAll();
    }


    @Override
    public void updateCategory(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryMapper.deleteById(categoryId);
    }


}
