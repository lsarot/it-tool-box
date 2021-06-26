package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.Category;
import com.sample.mybatis.model.persistence.entity.Client;
import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.entity.Order;

import java.util.List;

@Mapper
public interface ClientMapper {


    @Insert("INSERT INTO CLIENT (company_name, contact_tittle, contact_name, phone, fax, country, region, city, street, zip) VALUES (#{company_name}, #{contact_tittle}, #{contact_name}, #{phone}, #{fax}, #{country}, #{region}, #{city}, #{street}, #{zip})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Client client);


    @Select("select * from client")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "contact_tittle", column = "contact_tittle"),
            @Result(property = "contact_name", column = "contact_name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "country", column = "country"),
            @Result(property = "region", column = "region"),
            @Result(property = "city", column = "city"),
            @Result(property = "street", column = "street"),
            @Result(property = "zip", column = "zip"),
            @Result(property = "orders", javaType = List.class, column = "id", many = @Many(select = "getOrders"))
    })
    List<Client> getAll();


    @Select("select * from client where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "contact_tittle", column = "contact_tittle"),
            @Result(property = "contact_name", column = "contact_name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "country", column = "country"),
            @Result(property = "region", column = "region"),
            @Result(property = "city", column = "city"),
            @Result(property = "street", column = "street"),
            @Result(property = "zip", column = "zip"),
            @Result(property = "orders", javaType = List.class, column = "id", many = @Many(select = "getOrders"))
    })
    Client getById(int id);


    @Select("select * from `order` where id = #{id}")
    Order getOrders(int id);


    @Update("UPDATE client " +
            "SET company_name = #{company_name}," +
            " contact_tittle = #{contact_tittle}," +
            " contact_name = #{contact_name}," +
            " phone = #{phone}," +
            " fax = #{fax}," +
            " country = #{country}," +
            " region = #{region}," +
            " city = #{city}," +
            " street = #{street}," +
            " zip = #{zip}" +
            " WHERE id = #{id}")
    void update(Client client);


    @Delete("delete from client where id = #{id}")
    void deleteById(int id);




}
