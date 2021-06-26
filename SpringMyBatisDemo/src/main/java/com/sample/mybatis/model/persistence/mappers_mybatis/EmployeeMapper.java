package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.entity.Employee;
import com.sample.mybatis.model.persistence.entity.Order;

import java.util.List;

@Mapper
public interface EmployeeMapper {


    @Insert("INSERT INTO EMPLOYEE (tittle, name, surname, birthday, photo_url, entry_date, phone, country, region, city, street, zip, reports_to) VALUES (#{tittle}, #{name}, #{surname}, #{birthday}, #{photo_url}, #{entry_date}, #{phone}, #{country}, #{region}, #{city}, #{street}, #{zip}, #{reports_to})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Employee employee);


    @Select("select * from employee")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "tittle", column = "tittle"),
            @Result(property = "name", column = "name"),
            @Result(property = "surname", column = "surname"),
            @Result(property = "birthday", column = "birthday"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "entry_date", column = "entry_date"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "country", column = "country"),
            @Result(property = "region", column = "region"),
            @Result(property = "city", column = "city"),
            @Result(property = "street", column = "street"),
            @Result(property = "zip", column = "zip"),
            @Result(property = "reports_to", column = "reports_to"),
            @Result(property = "orders", javaType = List.class, column = "employee_id", many = @Many(select = "getOrders"))
    })
    List<Employee> getAll();


    @Select("select * from employee where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "tittle", column = "tittle"),
            @Result(property = "name", column = "name"),
            @Result(property = "surname", column = "surname"),
            @Result(property = "birthday", column = "birthday"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "entry_date", column = "entry_date"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "country", column = "country"),
            @Result(property = "region", column = "region"),
            @Result(property = "city", column = "city"),
            @Result(property = "street", column = "street"),
            @Result(property = "zip", column = "zip"),
            @Result(property = "reports_to", column = "reports_to"),
            @Result(property = "orders", javaType = List.class, column = "id", many = @Many(select = "getOrders"))
    })
    Employee getById(int id);


    @Select("select * from `order` where id = #{id}")
    Order getOrders(int id);


    @Update("UPDATE employee " +
            "SET tittle = #{tittle}," +
            " name = #{name}," +
            " surname = #{surname}," +
            " birthday = #{birthday}," +
            " photo_url = #{photo_url}," +
            " entry_date = #{entry_date}," +
            " phone = #{phone}," +
            " country = #{country}," +
            " region = #{region}," +
            " city = #{city}," +
            " street = #{street}," +
            " zip = #{zip}" +
            " reports_to = #{reports_to}," +
            " WHERE id = #{id}")
    void update(Employee employee);


    @Delete("delete from employee where id = #{id}")
    void deleteById(int id);
}
