package com.example.springsecdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springsecdemo.model.persistence.entity.Employee;
import com.example.springsecdemo.model.persistence.repository.IEmployeeRepo;

@Service
public class EmployeeService implements IEmployeeService {

	@Autowired
	private IEmployeeRepo empRepo;
	
	@Override
	public Optional<Employee> findById(Long id) {
		return empRepo.findById(id);
	}

	@Override
	public Employee save(Employee emp) {
		return empRepo.save(emp);
	}

	@Override
	public Iterable<Employee> findAll() {
		return empRepo.findAll();
	}
	
}
