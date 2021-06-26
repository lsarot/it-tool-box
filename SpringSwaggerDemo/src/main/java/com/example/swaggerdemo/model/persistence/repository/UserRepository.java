package com.example.swaggerdemo.model.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.swaggerdemo.model.persistence.entity.User;

//@RepositoryRestResource(collectionResourceRel = "users", path = "users") //POR SI QUEREMOS CAMBIAR EL NOMBRE AL PATH QUE MAPEA A UN REPOSITORY.. recordar que si no creamos un Controller, Spring automáticamente hará uno por nosotros!
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	//List<WebsiteUser> findByName(@Param("name") String name); //RECORDAR QUE usando findBy... creará una query que busque sobre esa columna en la tabla correspondiente!
	
}
