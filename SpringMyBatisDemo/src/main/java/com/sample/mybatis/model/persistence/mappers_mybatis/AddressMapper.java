package com.sample.mybatis.model.persistence.mappers_mybatis;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import com.sample.mybatis.model.persistence.entity.Address;
import com.sample.mybatis.model.persistence.entity.Contact;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * MAPPERS SON EL EQUIVALENTE A REPOSITORIES DE SPRING DATA CON HIBERNATE
 * 
 * REALMENTE ESTO CORRESPONDE A UN DAO
 * pero a diff de los Spring Data Repositories (que realmente son Daos y no repositories), esto nos permite crear Repositories con soltura, al poder hacer queries que retornen lo que queramos y jugar con varias tablas o Daos a la vez.
 * */

@Mapper
public interface AddressMapper {


	@Select("select * from address where id = #{_id}")
    @Results(value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "street", column = "street", jdbcType = JdbcType.VARCHAR),
            @Result(property = "contact_id", column = "contact_id"),
            @Result(property = "contact", javaType = Contact.class, 
            						column = "contact_id", //typeHandler = MyTypeHandler.class,
            						one = @One(select = "com.sample.mybatis.model.persistence.mappers_mybatis.ContactMapper.getContact", fetchType = FetchType.EAGER))
            						//en estos mapeos donde solicita a otro mapper, column es el parámetro del método y javaType lo que devuelve ese método (ie. Entidad o List)
            						//si al otro método se le pasan 2 params, se usa la sig sintaxis:   ie. column="{courseId=id,userId=user_id}"
    })
    Address getById(@Param("_id") int id); //@Param para cambiar el nombre del argumento
	
	
	@Select("select * from address")
	@Results(value = {
			@Result(property = "id", column = "id", id = true),
			@Result(property = "street", column = "street", jdbcType = JdbcType.VARCHAR),
			@Result(property = "contact_id", column = "contact_id"),
			@Result(property = "contact", column = "contact_id", javaType = Contact.class, one = @One(select = "com.sample.mybatis.model.persistence.mappers_mybatis.ContactMapper.getContact"))
					//column es la columna con el valor que le pasaremos al método getContact.
	})
	List<Address> getAll();
	
	
    @Insert("INSERT INTO ADDRESS (street, contact_id) VALUES (#{street}, #{contact_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(final Address address);


    @Update("UPDATE address SET street = #{street}, contact_id = #{contact_id} WHERE id = #{id}")
    void update(Address address);
    
    
    @Delete("delete from address where address_id = #{id}")
    void deleteById(int id);

    
    //LO COLOCAMOS EN ContactMapper PQ CONSULTA DE TABLA CONTACT
    /*@Select("select id c_id, name c_name, email c_email, phone c_phone from contact where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "c_id"),
            @Result(property = "name", column = "c_name"),
            @Result(property = "email", column = "c_email"),
            @Result(property = "phone", column = "c_phone"),
            @Result(property = "addresses", column = "c_id", javaType = List.class, many = @Many(select = "getContactAddresses"))
    })
    Contact getContact(int id);
    */
    
    
    @Select("select * from address where contact_id = #{id}")
    //aquí no usamos mapeo de campos a columnas pq usan los mismos nombres, y tampoco queremos recuperar más nada del Address.class
    List<Address> getContactAddresses(int id);

    
    
    
    //CUANDO ES PK COMPUESTA
    /*
    @Select("select * from post_locations")
    @Results({
        @Result(id=true, property = "id.postId", column = "post_id"),
        @Result(id=true, property = "id.locationId", column = "location_id"),
        //OTHER PROPERTIES...
      })
    public List<PostLocations> findAll();
    
    public class PostLocationsPK {
        private int postId, locationId;
        //getters, setters, constructors && equals
    }
    
    public class PostLocations {
    	private PostLocationsPK id;
    	//other properties...
    	//constructor, getters && setters....
	}
    */
    
    
    
    
    //CLASE NO NECESARIA EN ESTE PROYECTO... SÓLO ES PARA MOSTRAR USO DE @Result( typeHandler = MyTypeHandler.class )
    public static class MyTypeHandler implements org.apache.ibatis.type.TypeHandler {
		@Override
		public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {			
		}
		@Override
		public Object getResult(ResultSet rs, String columnName) throws SQLException {
			return null;
		}
		@Override
		public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
			return null;
		}
		@Override
		public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
			return null;
		}
    }
    
}
