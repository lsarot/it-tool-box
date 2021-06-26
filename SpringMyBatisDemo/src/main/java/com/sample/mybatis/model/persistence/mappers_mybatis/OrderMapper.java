package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.*;

import java.util.List;

@Mapper
public interface OrderMapper {


    @Insert("INSERT INTO `order` (issue_date, send_date, required_date, employee_id, client_id, courier_id) VALUES (#{issue_date}, #{send_date}, #{required_date}, #{employee_id}, #{client_id}, #{courier_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Order order);

    @Select("select * from `order`")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "issue_date", column = "issue_date"),
            @Result(property = "send_date", column = "send_date"),
            @Result(property = "required_date", column = "required_date"),
            @Result(property = "employee_id", column = "employee_id"),
            @Result(property = "client_id", column = "client_id"),
            @Result(property = "courier_id", column = "courier_id"),
            @Result(property = "employee", column = "employee_id", javaType = Employee.class, one = @One(select = "getEmployee")),
            @Result(property = "client", column = "client_id", javaType = Client.class, one = @One(select = "getClient")),
            @Result(property = "courier", column = "courier_id", javaType = Courier.class, one = @One(select = "getCourier"))
    })
    List<Order> getAll();


    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "issue_date", column = "issue_date"),
            @Result(property = "send_date", column = "send_date"),
            @Result(property = "required_date", column = "required_date"),
            @Result(property = "employee_id", column = "employee_id"),
            @Result(property = "client_id", column = "client_id"),
            @Result(property = "courier_id", column = "courier_id"),
            @Result(property = "employee", column = "employee_id", javaType = Employee.class, one = @One(select = "getEmployee")),
            @Result(property = "client", column = "client_id", javaType = Client.class, one = @One(select = "getClient")),
            @Result(property = "courier", column = "courier_id", javaType = Courier.class, one = @One(select = "getCourier"))
    })
    @Select("select * from `order` where id = #{id}")
    Order getById(int id);


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
            @Result(property = "reports_to", column = "reports_to")
    })
    Employee getEmployee(int id);


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
            @Result(property = "zip", column = "zip")
    })
    Client getClient(int id);


    @Select("select * from courier where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "company_name", column = "company_name"),
            @Result(property = "phone", column = "phone")
    })
    Courier getCourier(int id);


    @Update("UPDATE `order` " +
            "SET issue_date = #{issue_date}," +
            " send_date = #{send_date}," +
            " required_date = #{required_date}," +
            " employee_id = #{employee_id}," +
            " client_id = #{client_id}," +
            " courier_id = #{courier_id}" +
            " WHERE id = #{id}")
    void update(Order order);


    @Delete("delete from `order` where id = #{id}")
    void deleteById(int id);

}
