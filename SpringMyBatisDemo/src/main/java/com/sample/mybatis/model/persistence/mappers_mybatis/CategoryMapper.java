package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.Category;
import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.entity.Product;

import java.util.List;

@Mapper
public interface CategoryMapper {


    @Insert("INSERT INTO CATEGORY (category_name, description, image_url) VALUES (#{category_name}, #{description}, #{image_url})")
    @Options(keyProperty = "category_name")
    void insert(final Category category);


    @Select("select * from category")
    @Results(value = {
            @Result(property = "category_name", column = "category_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "image_url", column = "image_url"),
            @Result(property = "products", javaType = List.class, column = "id", many = @Many(select = "getProducts"))
    })
    List<Category> getAll();


    @Select("select * from category where category_name = #{id}")
    @Results(value = {
            @Result(property = "category_name", column = "category_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "image_url", column = "image_url"),
            @Result(property = "products", javaType = List.class, column = "id", many = @Many(select = "getProducts"))
    })
    Category getById(String id);



    @Select("select * from product where id = #{id}")
    Product getProducts(int id);



    @Update("UPDATE category " +
            "SET description = #{description}, image_url = #{image_url} " +
            "WHERE category_name = #{category_name}")
    void update(Category category);


    @Delete("delete from category where category_name = #{id}")
    void deleteById(String id);

}
