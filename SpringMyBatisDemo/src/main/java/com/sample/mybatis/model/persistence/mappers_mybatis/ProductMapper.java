package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.*;

import java.util.List;

@Mapper
public interface ProductMapper {


    @Insert("INSERT INTO PRODUCT (name, package_qty, unit_price, on_stock_qty, on_orders_qty, to_reorder_qty, discontinued, category_name, product_id) VALUES (#{name}, #{package_qty}, #{unit_price}, #{on_stock_qty}, #{on_orders_qty}, #{to_reorder_qty}, #{discontinued}, #{category_name}, #{product_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Product product);


    @Select("select * from product")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "package_qty", column = "package_qty"),
            @Result(property = "unit_price", column = "unit_price"),
            @Result(property = "on_stock_qty", column = "on_stock_qty"),
            @Result(property = "on_orders_qty", column = "on_orders_qty"),
            @Result(property = "to_reorder_qty", column = "to_reorder_qty"),
            @Result(property = "discontinued", column = "discontinued"),
            @Result(property = "category_name", column = "category_name"),
            @Result(property = "provider_id", column = "provider_id"),
            @Result(property = "provider", column = "id", javaType = Provider.class, one = @One(select = "getProvider")),
            @Result(property = "category", column = "category_name", javaType = Category.class, one = @One(select = "getCategory"))
    })
    List<Product> getAll();


    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "package_qty", column = "package_qty"),
            @Result(property = "unit_price", column = "unit_price"),
            @Result(property = "on_stock_qty", column = "on_stock_qty"),
            @Result(property = "on_orders_qty", column = "on_orders_qty"),
            @Result(property = "to_reorder_qty", column = "to_reorder_qty"),
            @Result(property = "discontinued", column = "discontinued"),
            @Result(property = "category_name", column = "category_name"),
            @Result(property = "provider_id", column = "provider_id"),
            @Result(property = "provider", column = "id", javaType = Provider.class, one = @One(select = "getProvider")),
            @Result(property = "category", column = "category_name", javaType = Category.class, one = @One(select = "getCategory"))
    })
    @Select("select * from product where id = #{id}")
    Product getById(int id);



    @Select("select * from provider where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "contact_tittle", column = "contact_tittle"),
            @Result(property = "contact_name", column = "contact_name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "web", column = "web"),
            @Result(property = "country", column = "country"),
            @Result(property = "region", column = "region"),
            @Result(property = "city", column = "city"),
            @Result(property = "street", column = "street"),
            @Result(property = "zip", column = "zip")
    })
    Provider getProvider(int id);


    @Select("select * from category where category_name = #{id}")
    @Results(value = {
            @Result(property = "category_name", column = "category_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "image_url", column = "image_url"),
    })
    Category getCategory(String id);



    @Update("UPDATE product " +
            "SET name = #{name}," +
            " package_qty = #{package_qty}," +
            " unit_price = #{unit_price}," +
            " on_stock_qty = #{on_stock_qty}," +
            " on_orders_qty = #{on_orders_qty}," +
            " to_reorder_qty = #{to_reorder_qty}," +
            " discontinued = #{discontinued}," +
            " category_name = #{category_name}," +
            " provider_id = #{provider_id}")
    void update(Product product);


    @Delete("delete from product where id = #{id}")
    void deleteById(int id);
    
}
