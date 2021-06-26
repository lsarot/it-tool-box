package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.*;

import java.util.List;

@Mapper
public interface CourierMapper {


    @Insert("INSERT INTO COURIER (company_name, phone) VALUES (#{company_name}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Courier courier);


    @Select("select * from courier")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "orders", javaType = List.class, column = "courier_id", many = @Many(select = "getOrders"))
    })
    List<Courier> getAll();


    @Select("select * from courier where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "orders", javaType = List.class, column = "courier_id", many = @Many(select = "getOrders"))
    })
    Courier getById(int id);


    @Select("select * from `order` where id = #{id}")
    Order getOrders(int id);


    @Update("UPDATE courier " +
            "SET company_name = #{company_name}," +
            " phone = #{phone}," +
            " WHERE id = #{id}")
    void update(Courier courier);


    @Delete("delete from courier where id = #{id}")
    void deleteById(int id);

}
