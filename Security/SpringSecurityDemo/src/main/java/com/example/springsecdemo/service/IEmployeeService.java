package com.example.springsecdemo.service;

import java.util.Optional;

import com.example.springsecdemo.model.persistence.entity.Employee;

public interface IEmployeeService {
	
	Optional<Employee> findById(Long id);
	 
    Employee save(Employee emp);
     
    Iterable<Employee> findAll();

}
