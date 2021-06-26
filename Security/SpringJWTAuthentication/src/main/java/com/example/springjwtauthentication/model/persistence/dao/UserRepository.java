package com.example.springjwtauthentication.model.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.springjwtauthentication.model.persistence.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	//NOTAR QUE NI HIZO FALTA USAR UNA QUERY.. use findByEntityfield
	User findByUsername(String username);
}
