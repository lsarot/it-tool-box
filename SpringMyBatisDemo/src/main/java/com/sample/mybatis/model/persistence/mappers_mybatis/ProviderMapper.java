package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.*;

import java.util.List;

@Mapper
public interface ProviderMapper {


    @Insert("INSERT INTO PROVIDER (company_name, contact_tittle, contact_name, phone, fax, web, country, region, city, street, zip) VALUES (#{company_name}, #{contact_tittle}, #{contact_name}, #{phone}, #{fax}, #{web}, #{country}, #{region}, #{city}, #{street}, #{zip)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Provider provider);


    @Select("select * from provider")
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
            @Result(property = "zip", column = "zip"),
            @Result(property = "products", javaType = List.class, column = "id", many = @Many(select = "getProducts"))
    })
    List<Provider> getAll();


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
            @Result(property = "zip", column = "zip"),
            @Result(property = "products", javaType = List.class, column = "id", many = @Many(select = "getProducts"))
    })
    @Select("select * from provider where id = #{id}")
    Provider getById(int id);



    @Select("select * from product where id = #{id}")
    Product getProducts(int id);



    @Update("UPDATE provider " +
            "SET company_name = #{company_name}," +
            " contact_tittle = #{contact_tittle}," +
            " contact_name = #{contact_name}," +
            " phone = #{phone}," +
            " fax = #{fax}," +
            " web = #{web}," +
            " country = #{country}," +
            " region = #{region}," +
            " city = #{city}," +
            " street = #{street}," +
            " zip = #{zip}" +
            " WHERE id = #{id}")
    void update(Provider provider);


    @Delete("delete from provider where id = #{id}")
    void deleteById(int id);

}
