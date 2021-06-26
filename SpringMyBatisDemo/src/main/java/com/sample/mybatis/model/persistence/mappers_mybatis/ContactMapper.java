package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;

import com.sample.mybatis.model.persistence.entity.Address;
import com.sample.mybatis.model.persistence.entity.Contact;

import java.util.List;

@Mapper
public interface ContactMapper {

    
	@Select("select * from contact where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "email", column = "email"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "addresses", javaType = List.class, 
            column = "id", 
            many = @Many(select = "com.sample.mybatis.model.persistence.mappers_mybatis.AddressMapper.getContactAddresses"))
    })
    Contact getById(int id);
    
    
	@Select("select * from contact")
	@Results(value = {
			@Result(property = "id", column = "id"),
			@Result(property = "name", column = "name"),
			@Result(property = "email", column = "email"),
			@Result(property = "phone", column = "phone"),
			@Result(property = "addresses", javaType = List.class, 
			column = "id", 
			many = @Many(select = "com.sample.mybatis.model.persistence.mappers_mybatis.AddressMapper.getContactAddresses"))
	})
	List<Contact> getAll();


    @Insert("INSERT INTO CONTACT (NAME, EMAIL, PHONE) VALUES (#{name}, #{email}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Contact contact);
    
    
    @Update("UPDATE contact " +
            "SET name = #{name}, phone = #{phone}, email = #{email} " +
            "WHERE id = #{id}")
    void update(Contact contact);


    @Delete("delete from contact where id = #{id}")
    void deleteById(int id);
    
    
    //TAMBIÉN FUNCIONA AQUÍ, PERO POR MANTENER UN ORDEN, SI SE CONSULTA A LA TABLA ADDRESS, LO COLOCAMOS EN AddressMapper
    //@Select("select * from address where contact_id = #{id}")
    //List<Address> getAddresses(int id);
    
    
    //se puede usar   Contact getById(int id);   pero queremos mostrar que podemos usar alias de campos en la consulta y mapear a estos.
    @Select("select id c_id, name c_name, email c_email, phone c_phone from contact where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "c_id"),
            @Result(property = "name", column = "c_name"),
            @Result(property = "email", column = "c_email"),
            @Result(property = "phone", column = "c_phone"),
            @Result(property = "addresses", javaType = List.class,
            column = "c_id", 
            many = @Many(select = "com.sample.mybatis.model.persistence.mappers_mybatis.AddressMapper.getContactAddresses"))
    })
    Contact getContact(int id);

}
